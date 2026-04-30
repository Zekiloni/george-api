package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.ConversionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ConversionJpaRepository extends JpaRepository<ConversionEntity, UUID> {

    List<ConversionEntity> findByCampaignId(String campaignId);
}
