package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.OutreachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface OutreachJpaRepository extends JpaRepository<OutreachEntity, UUID> {
    Stream<OutreachEntity> streamByCampaignId(UUID campaignId);
    Optional<OutreachEntity> findBySessionToken(String sessionToken);

    @Query("""
            SELECT COUNT(o) FROM OutreachEntity o
            WHERE o.campaign.serviceAccessId = :serviceAccessId
              AND o.dispatchedAt >= :since
              AND o.status <> com.zekiloni.george.platform.domain.model.campaign.outreach.OutreachStatus.FAILED
            """)
    long countDispatchedSinceByServiceAccessId(@Param("serviceAccessId") UUID serviceAccessId,
                                               @Param("since") OffsetDateTime since);
}
