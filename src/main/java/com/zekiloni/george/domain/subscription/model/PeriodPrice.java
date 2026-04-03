package com.zekiloni.george.domain.subscription.model;

import com.zekiloni.george.domain.common.model.Money;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PeriodPrice {
    private BillingPeriod period;
    private Money price;

    public static PeriodPrice of(BillingPeriod period, Money price) {
        return new PeriodPrice(period, price);
    }
}