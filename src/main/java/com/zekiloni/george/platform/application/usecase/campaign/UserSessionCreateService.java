package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.common.domain.model.Ref;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

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
    public Result handle(String token, String userAgent, String ipAddress, Enrichment enrichment) {
        // Anonymous endpoint — no JWT, no tenant context. Resolve the outreach
        // tenant-agnostically; the session is then stamped with the outreach's
        // tenant_id explicitly on the builder, so the row is owned by the right
        // tenant even though Hibernate's @TenantId resolver currently returns
        // SYSTEM (which is treated as root and bypasses the read filter for the
        // campaign/page lookups below).
        Outreach outreach = outreachRepository.findBySessionTokenAcrossTenants(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Outreach not found for token: " + token));

        Campaign campaign = campaignRepository.findById(outreach.getCampaignId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Campaign not found: " + outreach.getCampaignId()));

        // Geo block — the simulator passes the visitor's resolved country in
        // enrichment.country. When it matches one of the campaign's blocked
        // countries we throw 401 here; the controller propagates the status,
        // the simulator route handler maps it to a 404-style "Link unavailable"
        // response, and no UserSession row is ever created.
        rejectIfCountryBlocked(campaign, enrichment);

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
                .map(existing -> resume(existing, userAgent, ipAddress, enrichment))
                .orElseGet(() -> createFresh(outreach, fingerprint, userAgent, ipAddress, enrichment));

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
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Page not found: " + pageRef.getId()));

        return new Result(
                saved.getId(),
                saved.getWsToken(),
                saved.getSessionKey(),
                currentStep,
                flow.size(),
                page.getDefinition());
    }

    private UserSession resume(UserSession existing, String userAgent, String ipAddress, Enrichment enrichment) {
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
        applyEnrichment(existing, enrichment);
        return existing;
    }

    private UserSession createFresh(Outreach outreach, String fingerprint, String userAgent, String ipAddress, Enrichment enrichment) {
        UserSession session = UserSession.builder()
                .wsToken(generateWsToken())
                .sessionKey(generateSessionKey())
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
        applyEnrichment(session, enrichment);
        return session;
    }

    private void rejectIfCountryBlocked(Campaign campaign, Enrichment enrichment) {
        List<String> blocked = campaign.getBlockedCountries();
        if (blocked == null || blocked.isEmpty()) return;
        String visitor = enrichment == null ? null : enrichment.country();
        if (visitor == null) return;
        String visitorUpper = visitor.toUpperCase();
        boolean hit = blocked.stream()
                .filter(c -> c != null && !c.isBlank())
                .map(String::toUpperCase)
                .anyMatch(visitorUpper::equals);
        if (hit) {
            log.info("Refusing visit to campaign {} from blocked country {}", campaign.getId(), visitorUpper);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "country_blocked");
        }
    }

    private void applyEnrichment(UserSession session, Enrichment enrichment) {
        if (enrichment == null) return;
        if (enrichment.flags() != null && !enrichment.flags().isEmpty()) {
            session.setFlags(enrichment.flags());
        }
        if (enrichment.country() != null) session.setCountry(enrichment.country());
        if (enrichment.city() != null) session.setCity(enrichment.city());
        if (enrichment.asn() != null) session.setAsn(enrichment.asn());
        if (enrichment.asnOrg() != null) session.setAsnOrg(enrichment.asnOrg());
        if (enrichment.riskScore() != null) session.setRiskScore(enrichment.riskScore());
    }

    private String generateWsToken() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    /**
     * 32 random bytes → base64. Used by both ends as the symmetric AES-256-GCM
     * key for payload encryption. Server stores it so it can hand it to the
     * authorized operator later, but never decrypts payloads itself.
     */
    private String generateSessionKey() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
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
