package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSessionStatus;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.UserSessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntity, UUID> {

    Optional<UserSessionEntity> findByWsToken(String wsToken);

    /**
     * Cross-tenant ws-token lookup for the anonymous form submit endpoint
     * ({@code POST /api/v1/user-session/{wsToken}/submit}). Same pattern as
     * the visitor token flow — no JWT, no tenant context, ws-token is
     * randomly generated so collisions across tenants are not possible.
     */
    @Query(value = "SELECT * FROM user_sessions WHERE ws_token = :wsToken LIMIT 1", nativeQuery = true)
    Optional<UserSessionEntity> findByWsTokenAcrossTenants(@Param("wsToken") String wsToken);

    Optional<UserSessionEntity> findFirstByOutreach_IdAndFingerprintAndStatusInOrderByCreatedAtDesc(
            UUID outreachId, String fingerprint, Collection<UserSessionStatus> statuses);

    /**
     * Paginated session list for a campaign. Joined through outreach since
     * campaign_id isn't denormalized onto user_sessions.
     */
    @Query("SELECT s FROM UserSessionEntity s WHERE s.outreach.campaign.id = :campaignId")
    Page<UserSessionEntity> findByCampaignId(@Param("campaignId") UUID campaignId, Pageable pageable);

    @Query("SELECT s FROM UserSessionEntity s WHERE s.outreach.campaign.id = :campaignId AND s.status IN :statuses")
    Page<UserSessionEntity> findByCampaignIdAndStatusIn(
            @Param("campaignId") UUID campaignId,
            @Param("statuses") Collection<UserSessionStatus> statuses,
            Pageable pageable);

    @Query("""
            SELECT s.status, COUNT(s) FROM UserSessionEntity s
            WHERE s.outreach.campaign.id = :campaignId
            GROUP BY s.status
            """)
    List<Object[]> countByCampaignIdGroupedByStatus(@Param("campaignId") UUID campaignId);
}
