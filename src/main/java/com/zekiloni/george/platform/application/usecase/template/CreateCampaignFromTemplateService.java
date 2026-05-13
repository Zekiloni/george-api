package com.zekiloni.george.platform.application.usecase.template;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.template.CreateCampaignFromTemplateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.application.port.out.template.TemplateRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStatus;
import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.domain.model.page.PageStatus;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import com.zekiloni.george.platform.domain.model.template.Template;
import com.zekiloni.george.platform.domain.model.template.TemplateStep;
import com.zekiloni.george.platform.domain.model.template.TemplateVariable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Materializes a Template into a Campaign + tenant-owned Pages.
 *
 * <p>Cloning is the core trick: every step's PageDefinition is copied into a
 * fresh Page row owned by the calling tenant. Later edits to the original
 * template don't affect the running campaign — the campaign's flow points at
 * the clones. The campaign also remembers {@code sourceTemplateId} and
 * {@code sourceTemplateVersion} so the UI can later offer "your template
 * author published v2 — preview the diff?" without auto-overwriting.</p>
 *
 * <p>Variable substitution runs at clone time, not at render time, so cloned
 * Pages are "frozen" with the values the user picked. Re-running through the
 * library produces a separate Campaign with its own variable choices.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CreateCampaignFromTemplateService implements CreateCampaignFromTemplateUseCase {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*}}");

    private final TemplateRepositoryPort templateRepository;
    private final PageRepositoryPort pageRepository;
    private final CampaignRepositoryPort campaignRepository;
    private final TenantContext tenantContext;

    @Override
    @Transactional
    public Campaign handle(String templateId, String campaignName, Map<String, String> variables) {
        Template template = templateRepository.findById(templateId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));
        requireVisibleToCurrentTenant(template);

        if (template.getSteps() == null || template.getSteps().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Template has no steps");
        }

        Map<String, String> resolved = resolveVariables(template.getVariables(), variables);

        List<Ref> flow = template.getSteps().stream()
                .sorted(Comparator.comparingInt(TemplateStep::getOrder))
                .map(step -> cloneStepIntoPage(template, step, resolved))
                .map(saved -> Ref.builder()
                        .id(saved.getId())
                        .name(saved.getTitle())
                        .href("/page/" + saved.getId())
                        .build())
                .toList();

        Campaign campaign = Campaign.builder()
                .name(campaignName)
                // SCHEDULED but with no scheduledAt / recipients yet — the user
                // will fill those in via the campaign-edit flow before it ships.
                .status(CampaignStatus.SCHEDULED)
                .flow(flow)
                .tenantId(tenantContext.getTenantId())
                .sourceTemplateId(template.getId())
                .sourceTemplateVersion(template.getVersion())
                .build();
        Campaign saved = campaignRepository.save(campaign);

        templateRepository.incrementUsage(template.getId());
        log.info("Instantiated template {} → campaign {} ({} steps)",
                template.getId(), saved.getId(), flow.size());

        return saved;
    }

    /** Builds the final {name → value} map by overlaying user input on declared defaults. */
    private Map<String, String> resolveVariables(List<TemplateVariable> declared, Map<String, String> supplied) {
        Map<String, String> resolved = new HashMap<>();
        if (declared != null) {
            for (TemplateVariable v : declared) {
                String userValue = supplied == null ? null : supplied.get(v.getName());
                String value = userValue != null ? userValue : v.getDefaultValue();
                if (value == null && v.isRequired()) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Missing required variable: " + v.getName());
                }
                resolved.put(v.getName(), value == null ? "" : value);
            }
        }
        // Allow ad-hoc values the template didn't declare (useful for HTML
        // imports where the author edited the template directly).
        if (supplied != null) {
            for (var entry : supplied.entrySet()) {
                resolved.putIfAbsent(entry.getKey(), entry.getValue());
            }
        }
        return resolved;
    }

    private Page cloneStepIntoPage(Template template, TemplateStep step, Map<String, String> variables) {
        PageDefinition original = step.getDefinition();
        PageDefinition substituted = PageDefinition.builder()
                .html(substitute(original.getHtml(), variables))
                .css(substitute(original.getCss(), variables))
                .variables(new HashMap<>(variables))     // keep the chosen values for render-time fallback
                .form(original.getForm())
                .build();

        String stepName = step.getName() != null ? step.getName() : (template.getName() + " — step " + (step.getOrder() + 1));

        Page page = Page.builder()
                .title(stepName)
                .slug(deriveSlug(template, step))
                .status(PageStatus.DRAFT)
                .definition(substituted)
                .build();

        return pageRepository.save(page);
    }

    private static String substitute(String source, Map<String, String> variables) {
        if (source == null || source.isEmpty() || variables.isEmpty()) return source;
        Matcher m = VARIABLE_PATTERN.matcher(source);
        StringBuilder out = new StringBuilder();
        while (m.find()) {
            String key = m.group(1);
            String value = variables.getOrDefault(key, m.group(0));
            m.appendReplacement(out, Matcher.quoteReplacement(value));
        }
        m.appendTail(out);
        return out.toString();
    }

    private static String deriveSlug(Template template, TemplateStep step) {
        String base = template.getName() == null ? "page" : template.getName();
        String slug = base.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("(^-|-$)", "");
        return slug + "-" + (step.getOrder() + 1) + "-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private void requireVisibleToCurrentTenant(Template template) {
        if (template.getCategoryId() != null) return; // public
        String currentTenant = tenantContext.getTenantId();
        if (TenantContext.SYSTEM.equals(currentTenant)) return;
        if (template.getTenantId() == null || !template.getTenantId().equals(currentTenant)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found");
        }
    }
}
