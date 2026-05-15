package com.zekiloni.george.platform.infrastructure.in.web.dto.campaign;

import java.util.List;

public record CampaignFieldAnalyticsDto(
        int stepIndex,
        String pageName,
        List<FieldDto> fields
) {
    public record FieldDto(
            String name,
            double fillRate,
            Long avgTimeMs,
            List<TopValueDto> topValues
    ) {}

    public record TopValueDto(
            String value,
            long count
    ) {}
}
