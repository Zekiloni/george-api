package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.OutreachEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Repository
public interface OutreachJpaRepository extends JpaRepository<OutreachEntity, UUID> {
    Stream<OutreachEntity> streamByCampaignId(UUID campaignId);
    Optional<OutreachEntity> findBySessionToken(String sessionToken);
}
