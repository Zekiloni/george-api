package com.zekiloni.george.domain.subscription.model;

import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.domain.common.model.TimePeriod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Data
@Builder
public class Discount {
    private String code;
    private String description;

    private DiscountType type;

    private BigDecimal value;

    private TimePeriod validFor;

    private Set<BillingPeriod> applicablePeriods = new HashSet<>();

    public Money applyTo(Money original) {
        if (!isValidNow()) return original;

        return switch (type) {
            case PERCENTAGE -> original.multiply(1 - (value.doubleValue() / 100));
            case FIXED_AMOUNT -> original.subtract(Money.of(value));
            case FREE_MONTHS -> original;
            case FREE_PERIOD -> Money.ZERO;
        };
    }

    public boolean isValidNow() {
        return validFor != null && validFor.isValid();
    }
}