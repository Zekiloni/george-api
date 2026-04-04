package com.zekiloni.george.infrastructure.input.web.billing.dto;

import com.zekiloni.george.domain.billing.model.BillingPeriod;
import com.zekiloni.george.domain.billing.model.DiscountType;
import com.zekiloni.george.infrastructure.input.web.comon.dto.MoneyDto;
import com.zekiloni.george.infrastructure.input.web.comon.dto.TimePeriodDto;

import java.math.BigDecimal;
import java.util.Set;

public record DiscountCreateDto(
        String code,
        String description,
        DiscountType type,
        BigDecimal value,
        TimePeriodDto validFor,
        Set<BillingPeriod> applicablePeriods
) {
}

