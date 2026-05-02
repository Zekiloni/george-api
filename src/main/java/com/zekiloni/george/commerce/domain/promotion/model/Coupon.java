package com.zekiloni.george.commerce.domain.promotion.model;

import com.zekiloni.george.common.domain.model.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Code-based promotion applied at checkout. Discounts the unit subtotal of an order.
 *
 * <ul>
 *   <li>{@link CouponDuration#ONCE} — applies to the next single invoice only.</li>
 *   <li>{@link CouponDuration#FOREVER} — applies to every renewal invoice indefinitely.</li>
 *   <li>{@link CouponDuration#LIMITED_PERIODS} — applies to the next N invoices,
 *       where N = {@link #getDurationInPeriods()}.</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
    private String id;

    /** Customer-facing redemption code, e.g. "SAVE20", "LAUNCH50". Case-insensitive in lookups. */
    private String code;

    private String name;
    private String description;

    private CouponType type;
    private CouponDuration duration;

    /** For PERCENT: fraction in [0, 1) (0.20 = 20% off). Null for FIXED_AMOUNT. */
    private BigDecimal percent;

    /** For FIXED_AMOUNT: the flat money amount to subtract. Null for PERCENT. */
    private Money fixedAmount;

    /** Used only when duration = LIMITED_PERIODS. Number of invoice cycles the coupon applies to. */
    private Integer durationInPeriods;

    /** Optional minimum order subtotal before the coupon can apply. */
    private Money minOrderAmount;

    /**
     * Optional scope. Empty list = applies to ALL offerings. Otherwise — only those listed.
     */
    private List<String> appliesToOfferingIds;

    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    private Integer maxRedemptions;
    @Builder.Default
    private int redeemedCount = 0;

    private CouponStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
