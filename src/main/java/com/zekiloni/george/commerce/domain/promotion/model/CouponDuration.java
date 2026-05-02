package com.zekiloni.george.commerce.domain.promotion.model;

public enum CouponDuration {
    /** Applies to the next single invoice only. */
    ONCE,
    /** Applies to every renewal invoice indefinitely. */
    FOREVER,
    /** Applies to the next N invoices (N = Coupon.durationInPeriods). */
    LIMITED_PERIODS
}
