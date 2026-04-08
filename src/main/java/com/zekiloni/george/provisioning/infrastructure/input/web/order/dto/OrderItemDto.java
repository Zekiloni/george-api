package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.OfferingDto;

import java.time.OffsetDateTime;

public record OrderItemDto(
        String id,
        OfferingDto offering,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

