package com.zekiloni.george.platform.domain.model.campaign;

import java.util.List;

public record CampaignFieldAnalytics(
        int stepIndex,
        String pageName,
        List<Field> fields
) {
    public record Field(
            String name,
            double fillRate,
            Long avgTimeMs,
            List<TopValue> topValues
    ) {}

    public record TopValue(
            String value,
            long count
    ) {}
}
