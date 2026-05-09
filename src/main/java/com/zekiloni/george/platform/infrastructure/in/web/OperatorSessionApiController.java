package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.campaign.UserSessionCommandUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@RestController
@RequestMapping("${api.base.path:/api/v1}/operator/sessions")
@RequiredArgsConstructor
public class OperatorSessionApiController {

    private final UserSessionCommandUseCase commandUseCase;
    private final UserSessionRegistry registry;
    private final UserSessionRepositoryPort sessionRepository;
    private final TenantContext tenantContext;

    // Snapshot of currently-connected visitor sessions. Filtered by:
    //   • current tenant — admins only see their own tenant's sessions, never
    //     leak across tenants. SYSTEM tenant gets to see everything (used by
    //     internal admin tooling that opts in via X-Admin-Access).
    //   • optional campaignId query param — powers the campaign-detail "Live
    //     sessions" tab.
    @GetMapping
    public ResponseEntity<List<ActiveSessionDto>> listActiveSessions(
            @RequestParam(required = false) String campaignId
    ) {
        String currentTenant = tenantContext.getTenantId();
        List<ActiveSessionDto> sessions = registry.snapshot().stream()
                .filter(s -> visibleToTenant(s, currentTenant))
                .filter(s -> campaignId == null || Objects.equals(s.getCampaignId(), campaignId))
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
        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected != null) {
            if (!visibleToTenant(connected, tenantContext.getTenantId())) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(List.copyOf(connected.getBuffer()));
        }
        // Fall through to the persisted record. findById uses the @TenantId
        // filter, so a cross-tenant lookup naturally returns empty.
        UserSession persisted = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new NoSuchElementException("Session not found: " + sessionId));
        return ResponseEntity.ok(persisted.getEvents() == null ? List.<UserEvent>of() : persisted.getEvents());
    }

    @PostMapping("/{sessionId}/commands")
    public ResponseEntity<Void> sendCommand(@PathVariable String sessionId,
                                            @RequestBody UserEvent command) {
        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected != null && !visibleToTenant(connected, tenantContext.getTenantId())) {
            return ResponseEntity.notFound().build();
        }
        commandUseCase.send(sessionId, command);
        return ResponseEntity.accepted().build();
    }

    private static boolean visibleToTenant(ConnectedSession s, String currentTenant) {
        if (currentTenant == null) return false;
        if (TenantContext.SYSTEM.equals(currentTenant)) return true;
        return Objects.equals(s.getTenantId(), currentTenant);
    }

    public record ActiveSessionDto(
            String sessionId,
            UserSessionStatus status,
            String lastHeartbeatAt,
            int bufferSize,
            int operatorWatchers
    ) {}
}
