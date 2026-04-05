package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
import com.zekiloni.george.provisioning.domain.catalog.model.DurationUnit;

import java.math.BigDecimal;

public record OfferingPriceCreateDto(
        String label,
        MoneyDto unitPrice,
        Integer duration,
        DurationUnit durationUnit,
        BigDecimal discount
) {
}
