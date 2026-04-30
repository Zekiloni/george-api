package com.zekiloni.george.platform.application.usecase.campaign;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCommandUseCase;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.infrastructure.in.ws.bus.SessionEventBus;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserSessionCommandService implements UserSessionCommandUseCase {

    private final UserSessionRegistry registry;
    private final SessionEventBus eventBus;
    private final ObjectMapper objectMapper;
    private final AuditorAware<String> auditorAware;

    @Override
    public void send(String sessionId, UserEvent command) {
        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected != null && !connected.tryConsumeCommandToken()) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Command rate limit exceeded");
        }

        stamp(command);
        if (connected != null) {
            connected.appendEvent(command);
        }

        try {
            eventBus.publish(SessionEventBus.actionsChannel(sessionId), objectMapper.writeValueAsString(command));
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Failed to serialize command", e);
        }

        if (connected == null) {
            log.debug("No local visitor for session {}; command published to bus for remote instance", sessionId);
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
