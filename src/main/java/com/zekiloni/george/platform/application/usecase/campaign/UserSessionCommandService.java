package com.zekiloni.george.platform.application.usecase.campaign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCommandUseCase;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionCommandService implements UserSessionCommandUseCase {

    private final UserSessionRegistry registry;
    private final ObjectMapper objectMapper;
    private final AuditorAware<String> auditorAware;

    @Override
    public void send(String sessionId, UserEvent command) {
        ConnectedSession connected = registry.get(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not connected: " + sessionId));

        stamp(command);
        connected.appendEvent(command);

        WebSocketSession visitor = connected.getVisitorSocket();
        if (visitor == null || !visitor.isOpen()) {
            log.warn("Visitor socket not open for session {}; command dropped (no offline queue)", sessionId);
            return;
        }

        try {
            visitor.sendMessage(new TextMessage(objectMapper.writeValueAsString(command)));
        } catch (IOException e) {
            log.error("Failed to send command to visitor for session {}: {}", sessionId, e.getMessage());
        }
    }

    private void stamp(UserEvent command) {
        command.setIncoming(true);
        if (command.getId() == null) {
            command.setId(UUID.randomUUID().toString());
        }
        OffsetDateTime now = OffsetDateTime.now();
        if (command.getCreatedAt() == null) {
            command.setCreatedAt(now);
        }
        command.setUpdatedAt(now);
        String auditor = auditorAware.getCurrentAuditor().orElse(null);
        if (command.getCreatedBy() == null) {
            command.setCreatedBy(auditor);
        }
        command.setUpdatedBy(auditor);
    }
}
