package com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto;

import com.zekiloni.george.provisioning.domain.billing.model.BillingPeriod;
import com.zekiloni.george.provisioning.domain.billing.model.DiscountType;
import com.zekiloni.george.common.infrastructure.in.web.dto.TimePeriodDto;

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

