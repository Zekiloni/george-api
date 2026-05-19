package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.campaign.SessionQueryUseCase;
import com.zekiloni.george.platform.application.port.in.campaign.SessionQueryUseCase.ActiveSession;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCommandUseCase;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("${api.base.path:/api/v1}/operator/sessions")
@RequiredArgsConstructor
public class OperatorSessionApiController {

    private final SessionQueryUseCase queryUseCase;
    private final UserSessionCommandUseCase commandUseCase;

    @GetMapping
    public ResponseEntity<List<ActiveSessionDto>> listActiveSessions(
            @RequestParam(required = false) String campaignId) {
        return ResponseEntity.ok(queryUseCase.listActive(campaignId).stream()
                .map(OperatorSessionApiController::toDto)
                .toList());
    }

    @GetMapping("/{sessionId}/events")
    public ResponseEntity<List<UserEvent>> listEvents(@PathVariable String sessionId) {
        return ResponseEntity.ok(queryUseCase.findEvents(sessionId));
    }

    /**
     * Returns the per-session AES-GCM key the operator client needs to
     * decrypt visitor payloads and encrypt outgoing commands. Tenant-scoped.
     */
    @GetMapping("/{sessionId}/key")
    @PreAuthorize("@sessionQueryUseCase.ownsSession(#sessionId)")
    public ResponseEntity<SessionKeyDto> getKey(@PathVariable String sessionId) {
        return ResponseEntity.ok(new SessionKeyDto(queryUseCase.findSessionKey(sessionId)));
    }

    @PostMapping("/{sessionId}/commands")
    public ResponseEntity<Void> sendCommand(@PathVariable String sessionId,
                                            @RequestBody UserEvent command) {
        commandUseCase.send(sessionId, command);
        return ResponseEntity.accepted().build();
    }

    private static ActiveSessionDto toDto(ActiveSession s) {
        return new ActiveSessionDto(
                s.sessionId(),
                s.status(),
                s.lastHeartbeatAt().toString(),
                s.bufferSize(),
                s.operatorWatchers());
    }

    public record ActiveSessionDto(
            String sessionId,
            UserSessionStatus status,
            String lastHeartbeatAt,
            int bufferSize,
            int operatorWatchers
    ) {}

    public record SessionKeyDto(String key) {}
}
