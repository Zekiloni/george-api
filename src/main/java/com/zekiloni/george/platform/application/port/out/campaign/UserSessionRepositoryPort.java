package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserSessionRepositoryPort {
    UserSession save(UserSession session);

    Optional<UserSession> findById(String id);

    Optional<UserSession> findByWsToken(String wsToken);

    Optional<UserSession> findByWsTokenAcrossTenants(String wsToken);

    Optional<UserSession> findReusable(String outreachId, String fingerprint);

    /**
     * Paginated session list for a campaign, optionally filtered by status.
     * Empty/null status set returns all statuses.
     */
    Page<UserSession> findByCampaignId(String campaignId, Collection<UserSessionStatus> statuses, Pageable pageable);

    /**
     * Aggregate session count per status for a campaign. Statuses with no
     * matching rows simply aren't in the result map.
     */
    Map<UserSessionStatus, Long> countByCampaignGroupedByStatus(String campaignId);

    /**
     * Completed sessions for a campaign, newest-first by updatedAt (the
     * timestamp at which the session was transitioned to COMPLETED), capped
     * at {@code limit} rows. Events are loaded with the entity (JSONB on the
     * same row) — caller can walk them to extract SUBMIT payloads.
     */
    List<UserSession> findCompletedByCampaignId(String campaignId, int limit);
}
