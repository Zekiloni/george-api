package com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
import com.zekiloni.george.commerce.domain.catalog.model.DurationUnit;

import java.math.BigDecimal;

public record OfferingPriceCreateDto(
        String label,
        MoneyDto unitPrice,
        Integer duration,
        DurationUnit durationUnit,
        BigDecimal discount
) {
}
