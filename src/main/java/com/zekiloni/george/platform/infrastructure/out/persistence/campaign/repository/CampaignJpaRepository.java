package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.CampaignEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CampaignJpaRepository extends JpaRepository<CampaignEntity, UUID> {
    List<CampaignEntity> findByStatusAndScheduledAtLessThanEqual(
            com.zekiloni.george.platform.domain.model.campaign.CampaignStatus status,
            OffsetDateTime now
    );
}
