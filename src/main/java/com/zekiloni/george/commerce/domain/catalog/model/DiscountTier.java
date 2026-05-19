package com.zekiloni.george.commerce.domain.catalog.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class DiscountTier {
    private String id;
    private Integer fromUnits;
    private BigDecimal discount;

    public void setDiscount(BigDecimal discount) {
        if (discount != null) {
            if (discount.signum() < 0 || discount.compareTo(BigDecimal.ONE) >= 0) {
                throw new IllegalArgumentException(
                        "Discount tier value must be in [0, 1), got: " + discount);
            }
        }
        this.discount = discount;
    }
}
