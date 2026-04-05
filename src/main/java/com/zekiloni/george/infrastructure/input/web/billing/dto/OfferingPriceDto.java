package com.zekiloni.george.infrastructure.input.web.billing.dto;

import com.zekiloni.george.domain.billing.model.BillingPeriod;
import com.zekiloni.george.infrastructure.input.web.comon.dto.MoneyDto;

public record OfferingPriceDto(
        String id,
        BillingPeriod period,
        MoneyDto price
) {
}

