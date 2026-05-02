package com.zekiloni.george.commerce.domain.catalog.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.domain.catalog.exception.OfferingPricingException;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.PriceImpact;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * The "what" — a purchasable plan with its current price, characteristics, and lifecycle metadata.
 *
 * <p>Pricing model:
 * <pre>
 *   total = unitAmount × units × (1 - applicableDiscount(units))
 *         + Σ priceAdjustment(matchedCharacteristicValueSpec)
 * </pre>
 *
 * <p>Time-based ({@code timeUnit != null}) offerings drive {@code ServiceAccess.validTo}.
 * Quota-based offerings cap consumption (LEAD packs, EMAIL credits) — bounded by units, not time.
 *
 * <p>No grandfathering: customers always renew at the current catalog price. Historical
 * snapshots live on {@code InvoiceItem.totalAmount}.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offering {
    private String id;
    private String name;
    private String description;
    private String identifier;
    private ServiceSpecification serviceSpecification;
    private OfferingStatus status;

    private Money unitAmount;
    private String unitLabel;
    private ChronoUnit timeUnit;
    @Builder.Default
    private int intervalCount = 1;

    @Builder.Default
    private Integer minUnits = 1;
    private Integer maxUnits;

    @Builder.Default
    private TierMode tierMode = TierMode.NONE;
    private List<DiscountTier> tiers;

    /** Days before validTo to issue a renewal invoice. Null = use platform default. */
    private Integer renewalNoticeDays;

    /** Days after validTo expires (suspension) before termination. Null = use platform default. */
    private Integer gracePeriodDays;

    private List<CharacteristicSpecification> characteristicSpecification;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    /** Lowest displayable rate for catalog cards ("from $X"). */
    public Money getStartingPrice() {
        validateUnitAmount();
        BigDecimal deepest = tiers == null ? BigDecimal.ZERO : tiers.stream()
                .map(DiscountTier::getDiscount)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(BigDecimal.ZERO);
        if (deepest.signum() == 0) return unitAmount;
        return unitAmount.multiply(BigDecimal.ONE.subtract(deepest));
    }

    /** Total price for the given order item, billed as a NEW_PURCHASE. */
    public Money priceFor(OrderItem item) {
        return priceFor(item, InvoiceType.NEW_PURCHASE);
    }

    /**
     * Total price for the given order item, with characteristic adjustments filtered by
     * invoice type: {@code ONE_TIME} adjustments apply only on NEW_PURCHASE; {@code RECURRING}
     * applies on both NEW_PURCHASE and RENEWAL; {@code NONE} never affects price.
     */
    public Money priceFor(OrderItem item, InvoiceType invoiceType) {
        validateUnitAmount();
        int units = unitsOf(item);
        BigDecimal discount = applicableDiscount(units);

        Money unitSubtotal = unitAmount
                .multiply(units)
                .multiply(BigDecimal.ONE.subtract(discount));

        Money adjustments = sumCharacteristicAdjustments(item.getCharacteristic(), invoiceType);
        return unitSubtotal.add(adjustments);
    }

    public boolean isRecurring() {
        return timeUnit != null;
    }

    private int unitsOf(OrderItem item) {
        Integer units = item == null ? null : item.getUnits();
        if (units == null || units <= 0) {
            throw new OfferingPricingException(
                    "Order item for '" + nameOrId() + "' must have units > 0");
        }
        if (minUnits != null && units < minUnits) {
            throw new OfferingPricingException(
                    "Minimum " + minUnits + " " + unitLabelOrDefault() + " for '" + nameOrId() + "'");
        }
        if (maxUnits != null && units > maxUnits) {
            throw new OfferingPricingException(
                    "Maximum " + maxUnits + " " + unitLabelOrDefault() + " for '" + nameOrId() + "'");
        }
        return units;
    }

    private BigDecimal applicableDiscount(int units) {
        if (tiers == null || tiers.isEmpty() || tierMode == null || tierMode == TierMode.NONE) {
            return BigDecimal.ZERO;
        }
        if (tierMode == TierMode.VOLUME) {
            return tiers.stream()
                    .filter(t -> t.getFromUnits() != null && t.getDiscount() != null)
                    .filter(t -> units >= t.getFromUnits())
                    .map(DiscountTier::getDiscount)
                    .max(Comparator.naturalOrder())
                    .orElse(BigDecimal.ZERO);
        }
        // GRADUATED: reserved for future use
        return BigDecimal.ZERO;
    }

    private Money sumCharacteristicAdjustments(List<Characteristic> chars, InvoiceType invoiceType) {
        if (chars == null || chars.isEmpty() || characteristicSpecification == null) {
            return Money.of(currencyCode(), BigDecimal.ZERO);
        }
        Money sum = Money.of(currencyCode(), BigDecimal.ZERO);
        for (Characteristic c : chars) {
            Money adj = adjustmentFor(c, invoiceType);
            if (adj != null) sum = sum.add(adj);
        }
        return sum;
    }

    private Money adjustmentFor(Characteristic c, InvoiceType invoiceType) {
        for (CharacteristicSpecification spec : characteristicSpecification) {
            if (!spec.getName().equals(c.getName())) continue;
            if (!isApplicable(spec.getPriceImpact(), invoiceType)) return null;
            List<CharacteristicValueSpecification> values = spec.getCharacteristicValueSpecification();
            if (values == null) return null;
            for (CharacteristicValueSpecification v : values) {
                if (Objects.equals(v.getValue(), c.getValue()) && v.getPriceAdjustment() != null) {
                    return v.getPriceAdjustment();
                }
            }
        }
        return null;
    }

    private boolean isApplicable(PriceImpact impact, InvoiceType invoiceType) {
        if (impact == null || impact == PriceImpact.NONE) return false;
        if (impact == PriceImpact.RECURRING) return true;
        // ONE_TIME: only on initial purchase, never on renewals
        return invoiceType == InvoiceType.NEW_PURCHASE;
    }

    private void validateUnitAmount() {
        if (unitAmount == null || unitAmount.getCurrency() == null) {
            throw new OfferingPricingException(
                    "Offering '" + nameOrId() + "' has no unit amount");
        }
    }

    private String currencyCode() {
        return unitAmount.getCurrency().getCurrencyCode();
    }

    private String unitLabelOrDefault() {
        return unitLabel == null || unitLabel.isBlank() ? "units" : unitLabel;
    }

    public String nameOrId() {
        return name != null ? name : id;
    }
}
