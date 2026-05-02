package com.zekiloni.george.commerce.domain.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Step-discount tier on an {@link Offering}: when an order has at least
 * {@code fromUnits}, apply {@code discount} (a fraction in [0, 1)) to the
 * unit-price subtotal.
 *
 * <p>Tiers replace both volume tiers ("buy 1000+ leads, save 10%") and
 * commitment discounts ("commit to 12+ months, save 20%") — once
 * {@code OrderItem.units} is unified, they're the same concept.
 */
@Data
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
