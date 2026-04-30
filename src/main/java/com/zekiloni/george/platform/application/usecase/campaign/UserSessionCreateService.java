package com.zekiloni.george.platform.application.usecase.campaign;

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
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class UserSessionCreateService implements UserSessionCreateUseCase {
    private final OutreachRepositoryPort outreachRepository;
    private final CampaignRepositoryPort campaignRepository;
    private final PageRepositoryPort pageRepository;
    private final UserSessionRepositoryPort userSessionRepository;

    private static final SecureRandom RANDOM = new SecureRandom();

    @Override
    @Transactional
    public Result handle(String token, String userAgent, String ipAddress) {
        Outreach outreach = outreachRepository.findBySessionToken(token)
                .orElseThrow(() -> new RuntimeException("Outreach not found for token: " + token));

        Campaign campaign = campaignRepository.findById(outreach.getCampaignId())
                .orElseThrow(() -> new RuntimeException("Campaign not found: " + outreach.getCampaignId()));

        Page page = pageRepository.findById(campaign.getPage().getId())
                .orElseThrow(() -> new RuntimeException("Page not found: " + campaign.getPage().getId()));

        String fingerprint = generateFingerprint(token, userAgent, ipAddress);

        UserSession session = UserSession.builder()
                .wsToken(generateWsToken())
                .fingerprint(fingerprint)
                .userAgent(userAgent)
                .ipAddress(ipAddress)
                .outreach(outreach)
                .status(UserSessionStatus.ACTIVE)
                .viewCount(1)
                .lastActivityAt(OffsetDateTime.now())
                .build();

        UserSession saved = userSessionRepository.save(session);

        if (outreach.getStatus() == OutreachStatus.SENT) {
            outreach.setStatus(OutreachStatus.VISITED);
            outreachRepository.save(outreach);
        }

        return new Result(saved.getId(), saved.getWsToken(), page.getDefinition());
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
