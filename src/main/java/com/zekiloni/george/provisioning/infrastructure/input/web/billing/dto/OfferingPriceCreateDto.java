package com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.BillingPeriod;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;

public record OfferingPriceCreateDto(BillingPeriod period, MoneyDto price) {
}
