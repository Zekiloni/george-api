package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.CampaignEntity;
import org.mapstruct.Mapper;

@Mapper
public interface CampaignEntityMapper {
    CampaignEntity toEntity(com.zekiloni.george.platform.domain.model.campaign.Campaign campaign);

    com.zekiloni.george.platform.domain.model.campaign.Campaign toDomain(CampaignEntity campaignEntity);
}
