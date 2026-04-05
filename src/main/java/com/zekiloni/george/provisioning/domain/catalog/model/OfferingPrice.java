package com.zekiloni.george.provisioning.domain.catalog.model;

import com.zekiloni.george.common.domain.model.Money;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferingPrice {
    private String id;
    private String label;
    private Money unitPrice;
    private Integer duration;
    private DurationUnit durationUnit;
    private BigDecimal discount;

    public Money getEffectiveUnitPrice() {
        if (discount == null || discount.compareTo(BigDecimal.ZERO) == 0) {
            return unitPrice;
        }
        return unitPrice.multiply(BigDecimal.ONE.subtract(discount));
    }
}