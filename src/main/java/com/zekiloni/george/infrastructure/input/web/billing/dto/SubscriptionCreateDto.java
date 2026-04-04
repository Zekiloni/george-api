package com.zekiloni.george.infrastructure.input.web.billing.dto;

import java.time.OffsetDateTime;

public record SubscriptionCreateDto(
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        String planId
) {
}

