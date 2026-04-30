package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCommandUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("${api.base.path:/api/v1}/operator/sessions")
@RequiredArgsConstructor
public class OperatorSessionApiController {

    private final UserSessionCommandUseCase commandUseCase;
    private final UserSessionRegistry registry;
    private final UserSessionRepositoryPort sessionRepository;

    @GetMapping
    public ResponseEntity<List<ActiveSessionDto>> listActiveSessions() {
        List<ActiveSessionDto> sessions = registry.snapshot().stream()
                .map(s -> new ActiveSessionDto(
                        s.getSessionId(),
                        s.getStatus(),
                        Instant.ofEpochMilli(s.getLastHeartbeatAt().get()).toString(),
                        s.getBuffer().size(),
                        s.getOperatorSockets().size()))
                .toList();
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{sessionId}/events")
    public ResponseEntity<List<UserEvent>> listEvents(@PathVariable String sessionId) {
        return registry.get(sessionId)
                .map(connected -> ResponseEntity.ok(List.copyOf(connected.getBuffer())))
                .orElseGet(() -> sessionRepository.findById(sessionId)
                        .map(s -> ResponseEntity.ok(s.getEvents() == null ? List.<UserEvent>of() : s.getEvents()))
                        .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId)));
    }

    @PostMapping("/{sessionId}/commands")
    public ResponseEntity<Void> sendCommand(@PathVariable String sessionId,
                                            @RequestBody UserEvent command) {
        commandUseCase.send(sessionId, command);
        return ResponseEntity.accepted().build();
    }

    public record ActiveSessionDto(
            String sessionId,
            UserSessionStatus status,
            String lastHeartbeatAt,
            int bufferSize,
            int operatorWatchers
    ) {}
}
