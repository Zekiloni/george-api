package com.zekiloni.george.platform.infrastructure.in.ws.session;

import com.zekiloni.george.platform.application.port.out.campaign.CampaignRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.ConversionRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.OutreachRepositoryPort;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.conversion.Conversion;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.InteractionType;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSessionLifecycleService {

    private static final long ACTIVE_TO_IDLE_MS = 30_000;
    private static final long IDLE_TO_ABANDONED_MS = 60_000;

    private final UserSessionRegistry registry;
    private final UserSessionRepositoryPort sessionRepository;
    private final OutreachRepositoryPort outreachRepository;
    private final CampaignRepositoryPort campaignRepository;
    private final ConversionRepositoryPort conversionRepository;

    @Scheduled(fixedDelay = 10_000)
    public void sweep() {
        long now = Instant.now().toEpochMilli();
        List<String> toAbandon = new ArrayList<>();

        registry.snapshot().forEach(session -> {
            long sinceHeartbeat = now - session.getLastHeartbeatAt().get();
            UserSessionStatus current = session.getStatus();

            if (current == UserSessionStatus.ACTIVE && sinceHeartbeat > ACTIVE_TO_IDLE_MS) {
                session.setStatus(UserSessionStatus.IDLE);
                log.debug("Session {} → IDLE", session.getSessionId());
            } else if (current == UserSessionStatus.IDLE && sinceHeartbeat > ACTIVE_TO_IDLE_MS + IDLE_TO_ABANDONED_MS) {
                toAbandon.add(session.getSessionId());
            }
        });

        toAbandon.forEach(id -> finalize(id, UserSessionStatus.ABANDONED));
    }

    public void markCompleted(String sessionId) {
        finalize(sessionId, UserSessionStatus.COMPLETED);
    }

    private void finalize(String sessionId, UserSessionStatus terminalStatus) {
        ConnectedSession connected = registry.remove(sessionId).orElse(null);

        try {
            UserSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session != null) {
                session.setStatus(terminalStatus);
                if (connected != null) {
                    session.setEvents(new ArrayList<>(connected.getBuffer()));
                }
                session.setLastActivityAt(OffsetDateTime.now());
                sessionRepository.save(session);
                transitionOutreach(session, terminalStatus);
                if (terminalStatus == UserSessionStatus.COMPLETED) {
                    recordConversion(session);
                }
            }
        } catch (Exception e) {
            log.error("Failed to flush terminal session {}: {}", sessionId, e.getMessage(), e);
        } finally {
            if (connected != null) {
                closeSocket(connected.getVisitorSocket(), terminalStatus);
                connected.getOperatorSockets().forEach(ws -> closeSocket(ws, terminalStatus));
            }
        }

        log.info("Session {} → {}", sessionId, terminalStatus);
    }

    private void transitionOutreach(UserSession session, UserSessionStatus terminalStatus) {
        if (session.getOutreach() == null || session.getOutreach().getId() == null) return;

        OutreachStatus target = switch (terminalStatus) {
            case COMPLETED -> OutreachStatus.COMPLETED;
            case ABANDONED -> OutreachStatus.ABANDONED;
            default -> null;
        };
        if (target == null) return;

        outreachRepository.findById(session.getOutreach().getId())
                .ifPresent(outreach -> {
                    outreach.setStatus(target);
                    outreachRepository.save(outreach);
                });
    }

    @SuppressWarnings("unchecked")
    private void recordConversion(UserSession session) {
        try {
            String outreachId = session.getOutreach() != null ? session.getOutreach().getId() : null;
            Outreach outreach = outreachId != null
                    ? outreachRepository.findById(outreachId).orElse(null)
                    : null;
            String campaignId = outreach != null ? outreach.getCampaignId() : null;
            String pageId = campaignId != null
                    ? campaignRepository.findById(campaignId)
                    .map(Campaign::getPage)
                    .map(p -> p.getId())
                    .orElse(null)
                    : null;

            Map<String, Object> formData = session.getEvents() == null ? Map.of() : session.getEvents().stream()
                    .filter(e -> e.getType() == InteractionType.SUBMIT)
                    .reduce((first, second) -> second)
                    .map(UserEvent::getPayload)
                    .map(p -> (Map<String, Object>) p)
                    .orElse(Map.of());

            Conversion conversion = Conversion.builder()
                    .tenantId(session.getTenantId())
                    .sessionId(session.getId())
                    .outreachId(outreachId)
                    .campaignId(campaignId)
                    .pageId(pageId)
                    .formData(formData)
                    .ipAddress(session.getIpAddress())
                    .userAgent(session.getUserAgent())
                    .fingerprint(session.getFingerprint())
                    .convertedAt(OffsetDateTime.now())
                    .build();

            conversionRepository.save(conversion);
        } catch (Exception e) {
            log.error("Failed to record conversion for session {}: {}", session.getId(), e.getMessage(), e);
        }
    }

    private void closeSocket(WebSocketSession ws, UserSessionStatus reason) {
        if (ws == null || !ws.isOpen()) return;
        try {
            ws.close(new CloseStatus(4003, "session " + reason.name().toLowerCase()));
        } catch (IOException e) {
            log.warn("Failed to close socket on {}: {}", reason, e.getMessage());
        }
    }
}
