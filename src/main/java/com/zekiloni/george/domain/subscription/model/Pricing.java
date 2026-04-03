package com.zekiloni.george.domain.subscription.model;

import com.zekiloni.george.domain.common.model.Money;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;


@Data
public class Pricing {
    private Set<PeriodPrice> periodPrices = new HashSet<>();

    public Money getPriceForPeriod(BillingPeriod period) {
        return periodPrices.stream()
                .filter(pp -> pp.getPeriod() == period)
                .findFirst()
                .map(PeriodPrice::getPrice)
                .orElse(Money.ZERO);
    }

    public boolean hasPeriod(BillingPeriod period) {
        return periodPrices.stream().anyMatch(pp -> pp.getPeriod() == period);
    }

    public void addOrUpdatePrice(BillingPeriod period, Money price) {
        periodPrices.removeIf(pp -> pp.getPeriod() == period);
        periodPrices.add(PeriodPrice.of(period, price));
    }
}