package com.zekiloni.george.platform.domain.model.campaign;

import java.time.OffsetDateTime;

public record CampaignTimelinePoint(
        OffsetDateTime bucketStart,
        long sent,
        long visited,
        long completed
) {}
