package com.zekiloni.george.domain.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Value object for quantities with unit of measurement.
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Quantity {
    private BigDecimal amount;
    private String unit;

    public static Quantity of(BigDecimal amount, String unit) {
        return new Quantity(amount, unit);
    }

    public static Quantity of(int amount, String unit) {
        return new Quantity(BigDecimal.valueOf(amount), unit);
    }

    public static Quantity of(double amount, String unit) {
        return new Quantity(BigDecimal.valueOf(amount), unit);
    }

    public Quantity add(Quantity other) {
        validateSameUnit(other);
        return new Quantity(this.amount.add(other.amount), this.unit);
    }

    public Quantity subtract(Quantity other) {
        validateSameUnit(other);
        return new Quantity(this.amount.subtract(other.amount), this.unit);
    }

    public boolean isGreaterThan(Quantity other) {
        validateSameUnit(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isLessThan(Quantity other) {
        validateSameUnit(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    public static Quantity zero() {
        return new Quantity(BigDecimal.ZERO, "unit");
    }

    public boolean isZeroOrNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    private void validateSameUnit(Quantity other) {
        if (!this.unit.equals(other.unit)) {
            throw new IllegalArgumentException("Cannot operate on quantities with different units: " + this.unit + " vs " + other.unit);
        }
    }
}

