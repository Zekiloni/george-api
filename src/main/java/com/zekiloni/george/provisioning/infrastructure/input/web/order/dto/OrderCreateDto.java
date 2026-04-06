package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.DurationUnit;


public record OrderCreateDto(
        String offeringId,
        Integer quantity,
        Integer duration,
        DurationUnit durationUnit
) {
}

