package com.zekiloni.george.infrastructure.input.web.billing.dto;

import com.zekiloni.george.infrastructure.input.web.comon.dto.MoneyDto;

import java.time.OffsetDateTime;
import java.util.Set;

public record PlanDto(
        String id,
        String name,
        String description,
        String identifier,
        Set<PlanFeatureDto> features,
        Set<PeriodPriceDto> pricing,
        MoneyDto monthlyPrice,
        boolean isActive,
        boolean isPublic,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

