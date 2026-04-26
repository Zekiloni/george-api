package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.infrastructure.in.web.dto.CampaignCreateDto;
import org.mapstruct.Mapper;

@Mapper
public interface CampaignDtoMapper {
    Campaign toDomain(CampaignCreateDto campaignDto);
}
