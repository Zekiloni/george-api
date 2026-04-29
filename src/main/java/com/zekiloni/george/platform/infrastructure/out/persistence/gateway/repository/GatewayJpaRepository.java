package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity.GatewayEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GatewayJpaRepository extends JpaRepository<GatewayEntity, UUID> {

    List<GatewayEntity> findByType(com.zekiloni.george.platform.domain.model.gatway.GatewayType type);

    @Query("SELECT g FROM GatewayEntity g WHERE g.enabled = true")
    List<GatewayEntity> findByEnabled(boolean enabled);
}
