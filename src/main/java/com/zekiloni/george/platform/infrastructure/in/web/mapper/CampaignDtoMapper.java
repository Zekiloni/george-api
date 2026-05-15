package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.domain.model.campaign.CampaignFieldAnalytics;
import com.zekiloni.george.platform.domain.model.campaign.CampaignReferrer;
import com.zekiloni.george.platform.domain.model.campaign.CampaignResponse;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStats;
import com.zekiloni.george.platform.domain.model.campaign.CampaignStepAnalytics;
import com.zekiloni.george.platform.domain.model.campaign.CampaignTimelinePoint;
import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserEvent;
import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignFieldAnalyticsDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignReferrerDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignResponseDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignSessionDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignStatsDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignStepAnalyticsDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.CampaignTimelinePointDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.InteractionSignalDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.campaign.OutreachDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CampaignDtoMapper {
    Campaign toDomain(CampaignCreateDto campaignDto);

    CampaignDto toDto(Campaign campaign);
    OutreachDto toDto(Outreach outreach);
    InteractionSignalDto toDto(UserEvent signal);
    CampaignStatsDto toDto(CampaignStats stats);

    @Mapping(source = "id", target = "sessionId")
    CampaignSessionDto toSessionDto(UserSession session);

    CampaignResponseDto toResponseDto(CampaignResponse response);

    CampaignStepAnalyticsDto toDto(CampaignStepAnalytics analytics);
    CampaignFieldAnalyticsDto toDto(CampaignFieldAnalytics analytics);
    CampaignFieldAnalyticsDto.FieldDto toDto(CampaignFieldAnalytics.Field field);
    CampaignFieldAnalyticsDto.TopValueDto toDto(CampaignFieldAnalytics.TopValue value);
    CampaignTimelinePointDto toDto(CampaignTimelinePoint point);
    CampaignReferrerDto toDto(CampaignReferrer referrer);
}
