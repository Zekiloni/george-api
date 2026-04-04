package com.zekiloni.george.domain.billing.model;

import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.PeriodPriceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for {@link PeriodPriceEntity}
 */
@Data
@AllArgsConstructor
public class PeriodPrice {
    private BillingPeriod period;
    private Money price;

    public static PeriodPrice of(BillingPeriod period, Money price) {
        return new PeriodPrice(period, price);
    }
}