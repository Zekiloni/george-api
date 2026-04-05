package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import java.time.OffsetDateTime;

public record OrderCreateDto(
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        String offeringId
) {
}

