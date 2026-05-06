package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
