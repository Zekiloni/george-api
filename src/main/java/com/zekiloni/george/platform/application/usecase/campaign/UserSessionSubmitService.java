package com.zekiloni.george.platform.application.usecase.campaign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.common.infrastructure.security.AesGcmCipher;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionSubmitUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.InteractionType;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionLifecycleService;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionSubmitService implements UserSessionSubmitUseCase {

    private final UserSessionRepositoryPort sessionRepository;
    private final CampaignRepositoryPort campaignRepository;
    private final PageRepositoryPort pageRepository;
    private final UserSessionRegistry registry;
    private final UserSessionLifecycleService lifecycleService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public Result handle(String wsToken, Map<String, Object> formData) {
        UserSession session = sessionRepository.findByWsTokenAcrossTenants(wsToken)
                .orElseThrow(() -> new NoSuchElementException("Session not found for token"));

        formData = maybeDecrypt(session, formData);

        UserSessionStatus current = session.getStatus();
        if (current == UserSessionStatus.COMPLETED || current == UserSessionStatus.ABANDONED
                || current == UserSessionStatus.BLOCKED) {
            log.info("Submit rejected: session {} already in terminal status {}", session.getId(), current);
            return new Result(session.getId(), false, false, session.getCurrentStep(), 0, null);
        }

        UserEvent submitEvent = UserEvent.builder()
                .id(UUID.randomUUID().toString())
                .type(InteractionType.SUBMIT)
                .incoming(false)
                .payload(formData)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        ConnectedSession connected = registry.get(session.getId()).orElse(null);
        if (connected != null) {
            connected.appendEvent(submitEvent);
        }

        // Flush events to DB on every step submit. Without this, earlier step
        // payloads only live in the in-memory buffer until the session goes
        // terminal — so a mid-flow abandon would silently drop them.
        lifecycleService.persistEvents(session.getId());

        Campaign campaign = campaignRepository.findById(session.getOutreach().getCampaignId())
                .orElseThrow(() -> new IllegalStateException(
                        "Campaign not found: " + session.getOutreach().getCampaignId()));
        List<Ref> flow = campaign.getFlow();
        int totalSteps = flow == null ? 0 : flow.size();
        int nextStep = session.getCurrentStep() + 1;

        if (nextStep >= totalSteps) {
            // Last step submitted — terminate the session.
            lifecycleService.markCompleted(session.getId());
            return new Result(session.getId(), true, true, session.getCurrentStep(), totalSteps, null);
        }

        // Advance to the next step and return its page definition.
        Ref nextPageRef = flow.get(nextStep);
        Page nextPage = pageRepository.findById(nextPageRef.getId())
                .orElseThrow(() -> new IllegalStateException("Page not found: " + nextPageRef.getId()));

        session.setCurrentStep(nextStep);
        session.setLastActivityAt(OffsetDateTime.now());
        sessionRepository.save(session);

        PageDefinition def = nextPage.getDefinition();
        return new Result(session.getId(), true, false, nextStep, totalSteps, def);
    }

    private Map<String, Object> maybeDecrypt(UserSession session, Map<String, Object> formData) {
        if (formData == null || !Boolean.TRUE.equals(formData.get("$enc"))) {
            return formData;
        }
        String key = session.getSessionKey();
        if (key == null) {
            throw new IllegalStateException("Encrypted submit but session has no key");
        }
        String iv = (String) formData.get("iv");
        String ct = (String) formData.get("ct");
        if (iv == null || ct == null) {
            throw new IllegalArgumentException("Malformed encrypted envelope");
        }
        String json = AesGcmCipher.decrypt(key, iv, ct);
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new IllegalStateException("Decrypted submit payload not JSON object", e);
        }
    }
}
