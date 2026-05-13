package com.zekiloni.george.platform.application.port.in.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;

import java.time.OffsetDateTime;
import java.util.List;

// Read-side queries for the operator console: list active sessions (filtered
// by current tenant + optional campaign) and fetch a session's event timeline.
// The query implementation handles the registry/persistence fallback so the
// controller doesn't need to know which side serves which row.
public interface SessionQueryUseCase {

    List<ActiveSession> listActive(String campaignId);

    List<UserEvent> findEvents(String sessionId);

    /**
     * Returns the per-session AES-256-GCM key (base64) so the operator can
     * decrypt visitor payloads and encrypt outgoing commands. Tenant-scoped:
     * a key for a session in another tenant returns 404.
     */
    String findSessionKey(String sessionId);

    record ActiveSession(
            String sessionId,
            UserSessionStatus status,
            OffsetDateTime lastHeartbeatAt,
            int bufferSize,
            int operatorWatchers
    ) {}
}
