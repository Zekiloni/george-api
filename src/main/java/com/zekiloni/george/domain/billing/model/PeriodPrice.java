package com.zekiloni.george.domain.billing.model;

import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.PeriodPriceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link PeriodPriceEntity}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PeriodPrice {
    private String id;
    private BillingPeriod period;
    private Money price;
}