package com.zekiloni.george.infrastructure.input.web.billing.dto;

import java.time.OffsetDateTime;

public record OfferingCharacteristicDto(
        String id,
        String key,
        String name,
        String description,
        Object value,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

