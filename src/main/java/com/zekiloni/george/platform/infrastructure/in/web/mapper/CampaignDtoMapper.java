package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.InteractionSignalDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.OutreachDto;
import org.mapstruct.Mapper;

@Mapper
public interface CampaignDtoMapper {
    Campaign toDomain(CampaignCreateDto campaignDto);

    CampaignDto toDto(Campaign campaign);
    OutreachDto toDto(Outreach outreach);
    InteractionSignalDto toDto(UserEvent signal);
}
