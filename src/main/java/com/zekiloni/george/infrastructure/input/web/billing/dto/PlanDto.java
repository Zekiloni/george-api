package com.zekiloni.george.infrastructure.input.web.billing.dto;

import java.time.OffsetDateTime;
import java.util.Set;

public record PlanDto(
        String id,
        String name,
        String description,
        String identifier,
        Set<PlanFeatureDto> features,
        Set<PeriodPriceDto> pricing,
        boolean isActive,
        boolean isPublic,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

