package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import java.time.OffsetDateTime;

public record OrderDto(
        String id,
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        OfferingDto offering,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

