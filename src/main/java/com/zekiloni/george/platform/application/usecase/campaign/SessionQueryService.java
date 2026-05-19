package com.zekiloni.george.platform.application.usecase.campaign;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.campaign.SessionQueryUseCase;
import com.zekiloni.george.platform.application.port.out.campaign.UserSessionRepositoryPort;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.ConnectedSession;
import com.zekiloni.george.platform.infrastructure.in.ws.session.UserSessionRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;

// Operator-side session queries. Owns the tenant filtering + registry/DB
// fallback so callers don't need to know whether a session is currently
// connected or already persisted.
@Service
@RequiredArgsConstructor
public class SessionQueryService implements SessionQueryUseCase {

    private final UserSessionRegistry registry;
    private final UserSessionRepositoryPort sessionRepository;
    private final TenantContext tenantContext;

    @Override
    public List<ActiveSession> listActive(String campaignId) {
        String currentTenant = tenantContext.getTenantId();
        return registry.snapshot().stream()
                .filter(s -> visibleToTenant(s, currentTenant))
                .filter(s -> campaignId == null || Objects.equals(s.getCampaignId(), campaignId))
                .map(SessionQueryService::toView)
                .toList();
    }

    @Override
    public String findSessionKey(String sessionId) {
        // Persistence is the source of truth for the key. The repo's findById
        // is @TenantId-filtered, so a cross-tenant lookup naturally returns
        // empty — we surface that as 404 to avoid leaking session existence.
        // Live sessions in the registry don't carry the key; always go to DB.
        UserSession persisted = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        String key = persisted.getSessionKey();
        if (key == null || key.isBlank()) {
            // Legacy session created before E2E was rolled out — operator
            // client treats absent key as "this session is plaintext".
            throw new ResponseStatusException(HttpStatus.NO_CONTENT, "Session has no encryption key");
        }
        return key;
    }

    @Override
    public boolean ownsSession(String sessionId) {
        String currentTenant = tenantContext.getTenantId();
        if (TenantContext.SYSTEM.equals(currentTenant)) return true;
        return sessionRepository.findById(sessionId)
                .map(s -> Objects.equals(s.getTenantId(), currentTenant))
                .orElse(false);
    }

    @Override
    public List<UserEvent> findEvents(String sessionId) {
        // Live session in the registry — return the in-memory buffer.
        ConnectedSession connected = registry.get(sessionId).orElse(null);
        if (connected != null) {
            if (!visibleToTenant(connected, tenantContext.getTenantId())) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found");
            }
            return List.copyOf(connected.getBuffer());
        }
        // Terminal session — fall back to the persisted record. findById
        // is @TenantId-filtered, so a cross-tenant lookup returns empty.
        UserSession persisted = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Session not found"));
        return persisted.getEvents() == null ? List.<UserEvent>of() : persisted.getEvents();
    }

    private static boolean visibleToTenant(ConnectedSession s, String currentTenant) {
        if (currentTenant == null) return false;
        if (TenantContext.SYSTEM.equals(currentTenant)) return true;
        return Objects.equals(s.getTenantId(), currentTenant);
    }

    private static ActiveSession toView(ConnectedSession s) {
        return new ActiveSession(
                s.getSessionId(),
                s.getStatus(),
                OffsetDateTime.ofInstant(Instant.ofEpochMilli(s.getLastHeartbeatAt().get()), ZoneOffset.UTC),
                s.getBuffer().size(),
                s.getOperatorSockets().size());
    }
}
