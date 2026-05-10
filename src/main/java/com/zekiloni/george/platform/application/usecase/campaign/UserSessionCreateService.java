package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCreateUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.domain.model.page.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserSessionCreateService implements UserSessionCreateUseCase {
    private final OutreachRepositoryPort outreachRepository;
    private final CampaignRepositoryPort campaignRepository;
    private final PageRepositoryPort pageRepository;
    private final UserSessionRepositoryPort userSessionRepository;

    private static final SecureRandom RANDOM = new SecureRandom();


    @Override
    public Result handle(String token, String userAgent, String ipAddress) {
        // Step 1 (no surrounding TX): tenant-agnostic native lookup of the
        // outreach so we can learn who owns this visitor flow.
        Outreach outreach = outreachRepository.findBySessionTokenAcrossTenants(token)
                .orElseThrow(() -> new RuntimeException("Outreach not found for token: " + token));

        // Step 3: run the rest inside a fresh TX. New Session, new tenant
        // resolution → INSERTs stamp the right tenant_id, SELECTs fi0lter on it.
        return doHandle(token, outreach, userAgent, ipAddress);
    }

    private Result doHandle(String token, Outreach outreach, String userAgent, String ipAddress) {
        Campaign campaign = campaignRepository.findById(outreach.getCampaignId())
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + outreach.getCampaignId()));

        List<Ref> flow = campaign.getFlow();
        if (flow == null || flow.isEmpty()) {
            throw new IllegalStateException("Campaign " + campaign.getId() + " has empty flow");
        }

        String fingerprint = generateFingerprint(token, userAgent, ipAddress);

        // Try to resume an existing non-terminal session for this same visitor
        // (same outreach + same fingerprint). If found, rotate its ws-token to
        // invalidate any stale WS connection and bring it back to ACTIVE so
        // the visitor picks up at the step they left off on.
        UserSession session = userSessionRepository.findReusable(outreach.getId(), fingerprint)
                .map(existing -> resume(existing, userAgent, ipAddress))
                .orElseGet(() -> createFresh(outreach, fingerprint, userAgent, ipAddress));

        UserSession saved = userSessionRepository.save(session);

        if (outreach.getStatus() == OutreachStatus.SENT) {
            outreach.setStatus(OutreachStatus.VISITED);
            outreachRepository.save(outreach);
        }

        // Resolve the page for the resumed step (clamp to flow bounds in case
        // the campaign's flow was edited between visits).
        int currentStep = Math.max(0, Math.min(saved.getCurrentStep(), flow.size() - 1));
        Ref pageRef = flow.get(currentStep);
        Page page = pageRepository.findById(pageRef.getId())
                .orElseThrow(() -> new RuntimeException("Page not found: " + pageRef.getId()));

        return new Result(
                saved.getId(),
                saved.getWsToken(),
                currentStep,
                flow.size(),
                page.getDefinition());
    }

    private UserSession resume(UserSession existing, String userAgent, String ipAddress) {
        log.info("Resuming session {} (was {}, step {})",
                existing.getId(), existing.getStatus(), existing.getCurrentStep());
        existing.setWsToken(generateWsToken());
        existing.setStatus(UserSessionStatus.ACTIVE);
        existing.setViewCount(existing.getViewCount() + 1);
        existing.setLastActivityAt(OffsetDateTime.now());
        // Refresh UA/IP — they may have rotated since the original visit
        // (e.g. mobile network handoff). Fingerprint stayed stable enough to
        // match, so we accept the new values.
        existing.setUserAgent(userAgent);
        existing.setIpAddress(ipAddress);
        return existing;
    }

    private UserSession createFresh(Outreach outreach, String fingerprint, String userAgent, String ipAddress) {
        return UserSession.builder()
                .wsToken(generateWsToken())
                .fingerprint(fingerprint)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .outreach(outreach)
                .status(UserSessionStatus.ACTIVE)
                .viewCount(1)
                .currentStep(0)
                .tenantId(outreach.getTenantId())
                .lastActivityAt(OffsetDateTime.now())
                .build();
    }

    private String generateWsToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String generateFingerprint(String token, String userAgent, String ipAddress) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = token + "|" + (userAgent != null ? userAgent : "") + "|" + (ipAddress != null ? ipAddress : "");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }
}
