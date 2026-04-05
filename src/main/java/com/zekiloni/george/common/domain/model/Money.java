package com.zekiloni.george.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Money {
    private static final Set<String> AVAILABLE_CURRENCY_CODES;

    static {
        AVAILABLE_CURRENCY_CODES = Currency.getAvailableCurrencies()
                .stream()
                .map(Currency::getCurrencyCode)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Checks if a given currency code is available in the set of all available currencies.
     *
     * @param currencyCode The 3-letter currency code.
     * @return true if the code is available, false otherwise.
     */
    public static boolean isCurrencyAvailable(String currencyCode) {
        return AVAILABLE_CURRENCY_CODES.contains(currencyCode);
    }

    private Currency currency;
    private BigDecimal amount;

    public static Money ZERO = new Money(Currency.getInstance("USD"), BigDecimal.ZERO);

    public static Money of(String currencyCode, BigDecimal amount) {
        if (!isCurrencyAvailable(currencyCode)) {
            throw new IllegalArgumentException("Currency code " + currencyCode + " is not available");
        }

        return new Money(Currency.getInstance(currencyCode), amount);
    }

    public static Money of(BigDecimal amount) {
        return of("USD", amount);
    }

    public Money add(Money other) {
        if (!isCurrencyAvailable(other.currency.getCurrencyCode())) {
            throw new IllegalArgumentException("Currency code " + other.currency.getCurrencyCode() + " is not available");
        }

        return new Money(this.currency, this.amount.add(other.amount));
    }

    public Money subtract(Money other) {
        if (!isCurrencyAvailable(other.currency.getCurrencyCode())) {
            throw new IllegalArgumentException("Currency code " + other.currency.getCurrencyCode() + " is not available");
        }
        return new Money(this.currency, this.amount.subtract(other.amount));
    }

    public Money multiply(double factor) {
        return new Money(this.currency, this.amount.multiply(BigDecimal.valueOf(factor)));
    }

    public boolean isGreaterThan(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("Cannot compare money with different currencies");
        }

        return this.amount.compareTo(other.amount) > 0;
    }


    @Override
    public String toString() {
        return String.format("%s %.2f", currency.getCurrencyCode(), amount);
    }
}
