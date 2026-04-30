package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.UserSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionJpaRepository extends JpaRepository<UserSessionEntity, UUID> {

    Optional<UserSessionEntity> findByWsToken(String wsToken);
}
