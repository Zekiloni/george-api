package com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto;

import com.zekiloni.george.provisioning.domain.billing.model.BillingPeriod;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;

public record OfferingPriceDto(
        String id,
        BillingPeriod period,
        MoneyDto price
) {
}

