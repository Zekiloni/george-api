package com.zekiloni.george.platform.application.port.out.campaign;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.stream.Stream;

public interface OutreachRepositoryPort {
    Outreach save(Outreach outreach);
    List<Outreach> saveAll(List<Outreach> outreach);

    Stream<Outreach> findByCampaignId(String campaignId);
    Optional<Outreach> findBySessionToken(String sessionToken);

    /**
     * Tenant-agnostic lookup for the anonymous visitor token endpoint.
     * Use only on the public {@code /user-session/{token}} read path —
     * after loading, set the tenant context from the outreach's tenant
     * so subsequent reads (campaign, page) re-engage tenant scoping.
     */
    Optional<Outreach> findBySessionTokenAcrossTenants(String sessionToken);

    Optional<Outreach> findById(String id);

    long countDispatchedSinceByServiceAccessId(String serviceAccessId, OffsetDateTime since);

    /**
     * Outreach delivery funnel for a campaign: count of recipients per status.
     * Statuses with zero rows simply aren't in the result map.
     */
    Map<OutreachStatus, Long> countByCampaignGroupedByStatus(String campaignId);
}
