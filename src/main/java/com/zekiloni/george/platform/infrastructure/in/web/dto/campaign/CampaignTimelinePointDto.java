package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import java.time.OffsetDateTime;

public record CampaignTimelinePointDto(
        OffsetDateTime bucketStart,
        long sent,
        long visited,
        long completed
) {}
