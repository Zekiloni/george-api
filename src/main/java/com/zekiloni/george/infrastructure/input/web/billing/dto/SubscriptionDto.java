package com.zekiloni.george.infrastructure.input.web.billing.dto;

import java.time.OffsetDateTime;

public record SubscriptionDto(
        String id,
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        PlanDto plan,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

