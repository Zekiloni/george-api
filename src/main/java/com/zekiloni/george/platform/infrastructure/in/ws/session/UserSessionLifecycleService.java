package com.zekiloni.george.platform.infrastructure.in.ws.session;

import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
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

@Component
@RequiredArgsConstructor
@Slf4j
public class UserSessionLifecycleService {

    private static final long ACTIVE_TO_IDLE_MS = 30_000;
    private static final long IDLE_TO_ABANDONED_MS = 60_000;

    private final UserSessionRegistry registry;
    private final UserSessionRepositoryPort sessionRepository;

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
        if (connected == null) return;

        try {
            UserSession session = sessionRepository.findById(sessionId).orElse(null);
            if (session != null) {
                session.setStatus(terminalStatus);
                session.setEvents(new ArrayList<>(connected.getBuffer()));
                session.setLastActivityAt(OffsetDateTime.now());
                sessionRepository.save(session);
            }
        } catch (Exception e) {
            log.error("Failed to flush terminal session {}: {}", sessionId, e.getMessage(), e);
        } finally {
            closeSocket(connected.getVisitorSocket(), terminalStatus);
            connected.getOperatorSockets().forEach(ws -> closeSocket(ws, terminalStatus));
        }

        log.info("Session {} → {}", sessionId, terminalStatus);
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
