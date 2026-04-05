package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

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

