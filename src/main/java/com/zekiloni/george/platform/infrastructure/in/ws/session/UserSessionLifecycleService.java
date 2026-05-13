package com.zekiloni.george.platform.infrastructure.in.ws.session;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
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

    // Heartbeats arrive every ~10s; we tolerate one missed beat before nudging
    // ACTIVE→IDLE, then 60s of silence past IDLE before deciding the visitor
    // truly abandoned. Tab-close detection is immediate via the WS close hook
    // — these timers only matter if the close handler doesn't fire (network
    // drop, killed browser, etc.).
    private static final long ACTIVE_TO_IDLE_MS = 30_000;
    private static final long IDLE_TO_ABANDONED_MS = 60_000;
    // Keep terminal sessions in the registry for a few minutes after they
    // finish, so operators on the campaign-detail view can still see their
    // final state instead of the row vanishing the instant the visitor submits.
    private static final long TERMINAL_GRACE_MS = 5 * 60_000;

    private final UserSessionRegistry registry;
    private final UserSessionRepositoryPort sessionRepository;
    private final OutreachRepositoryPort outreachRepository;
    private final CampaignRepositoryPort campaignRepository;
    private final ConversionRepositoryPort conversionRepository;
    private final TenantContext tenantContext;

    @Scheduled(fixedDelay = 10_000)
    public void sweep() {
        long now = Instant.now().toEpochMilli();
        List<String> toAbandon = new ArrayList<>();
        List<String> toEvict = new ArrayList<>();

        registry.snapshot().forEach(session -> {
            UserSessionStatus current = session.getStatus();
            long sinceHeartbeat = now - session.getLastHeartbeatAt().get();

            if (current == UserSessionStatus.COMPLETED || current == UserSessionStatus.ABANDONED
                    || current == UserSessionStatus.BLOCKED) {
                long sinceTerminal = now - session.getLastHeartbeatAt().get();
                if (sinceTerminal > TERMINAL_GRACE_MS) {
                    toEvict.add(session.getSessionId());
                }
                return;
            }

            if (current == UserSessionStatus.ACTIVE && sinceHeartbeat > ACTIVE_TO_IDLE_MS) {
                session.setStatus(UserSessionStatus.IDLE);
                log.debug("Session {} → IDLE", session.getSessionId());
            } else if (current == UserSessionStatus.IDLE && sinceHeartbeat > ACTIVE_TO_IDLE_MS + IDLE_TO_ABANDONED_MS) {
                toAbandon.add(session.getSessionId());
            }
        });

        toAbandon.forEach(this::markAbandoned);
        toEvict.forEach(registry::remove);
    }

    public void markCompleted(String sessionId) {
        withTenantOf(sessionId, () -> finalize(sessionId, UserSessionStatus.COMPLETED));
    }

    public void markAbandoned(String sessionId) {
        withTenantOf(sessionId, () -> finalize(sessionId, UserSessionStatus.ABANDONED));
    }

    /**
     * Persists the in-memory event buffer to the user_sessions row without
     * transitioning the session to a terminal status. Called on each step
     * submit so we don't lose form data if the visitor abandons mid-flow.
     */
    public void persistEvents(String sessionId) {
        withTenantOf(sessionId, () -> doPersistEvents(sessionId));
    }

    private void doPersistEvents(String sessionId) {
        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected == null) return;
        try {
            UserSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session == null) return;
            session.setEvents(new ArrayList<>(connected.getBuffer()));
            session.setLastActivityAt(OffsetDateTime.now());
            sessionRepository.save(session);
        } catch (Exception e) {
            log.warn("Failed to persist events for session {}: {}", sessionId, e.getMessage());
        }
    }

    /**
     * Sets {@link TenantContext} to the session's owning tenant for the
     * duration of the action, then restores the previous value. The scheduled
     * sweep, WS-triggered terminal transitions, and step-submit persistence
     * all run without a JWT-derived tenant context — without this wrap they
     * fall through to {@code TenantContext.SYSTEM} and either silently bypass
     * the @TenantId filter (root behaviour) or stamp new rows with SYSTEM.
     * Both outcomes mask cross-tenant bugs, so we set it explicitly.
     */
    private void withTenantOf(String sessionId, Runnable action) {
        String tenantId = registry.get(sessionId)
                .map(ConnectedSession::getTenantId)
                .filter(t -> t != null && !t.isBlank())
                .orElse(null);
        if (tenantId == null) {
            action.run();
            return;
        }
        String previous = tenantContext.getTenantId();
        try {
            tenantContext.setTenantId(tenantId);
            action.run();
        } finally {
            tenantContext.setTenantId(previous);
        }
    }

    private void finalize(String sessionId, UserSessionStatus terminalStatus) {
        // Look up but DON'T remove from registry — operators should still see
        // the session in its terminal state for TERMINAL_GRACE_MS. The sweep
        // evicts later.
        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected != null) {
            UserSessionStatus current = connected.getStatus();
            if (current == UserSessionStatus.COMPLETED || current == UserSessionStatus.BLOCKED) {
                // Already in a stronger terminal — don't downgrade COMPLETED → ABANDONED.
                return;
            }
            connected.setStatus(terminalStatus);
            connected.touchHeartbeat(); // reset grace timer so the row persists for the full window
        }

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
                    .map(Campaign::getFlow)
                    .filter(flow -> flow != null && !flow.isEmpty())
                    .map(flow -> flow.get(0))
                    .map(p -> p.getId())
                    .orElse(null)
                    : null;

            // Merge every step's SUBMIT payload — earlier steps would otherwise
            // be lost if a flow has more than one form. Later step values
            // overwrite earlier ones on key conflict (intentional — last
            // wins).
            Map<String, Object> formData = new java.util.HashMap<>();
            if (session.getEvents() != null) {
                for (UserEvent ev : session.getEvents()) {
                    if (ev.getType() == InteractionType.SUBMIT && ev.getPayload() instanceof Map<?, ?> payload) {
                        ((Map<String, Object>) payload).forEach(formData::put);
                    }
                }
            }

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
