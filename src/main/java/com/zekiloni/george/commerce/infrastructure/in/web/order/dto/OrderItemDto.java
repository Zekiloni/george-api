package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;

import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingDto;

import java.time.OffsetDateTime;

public record OrderItemDto(
        String id,
        OfferingDto offering,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

