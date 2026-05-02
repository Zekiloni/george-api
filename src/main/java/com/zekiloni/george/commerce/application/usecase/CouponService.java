package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.application.port.out.promotion.CouponRepositoryPort;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.domain.promotion.model.CouponDuration;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import com.zekiloni.george.commerce.domain.promotion.model.CouponType;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * Resolves and applies coupons.
 *
 * - Validates a coupon code against status, validity window, redemption limits.
 * - Determines whether the coupon applies to a given offering.
 * - Computes the discounted total given a subtotal.
 * - Tracks redemption (incremented on invoice PAID, not at order creation).
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepositoryPort repository;

    public Optional<Coupon> findApplicable(String code) {
        return repository.findByCode(code).filter(this::isUsable);
    }

    public boolean isUsable(Coupon coupon) {
        if (coupon == null) return false;
        if (coupon.getStatus() != CouponStatus.ACTIVE) return false;
        OffsetDateTime now = OffsetDateTime.now();
        if (coupon.getValidFrom() != null && coupon.getValidFrom().isAfter(now)) return false;
        if (coupon.getValidTo() != null && !coupon.getValidTo().isAfter(now)) return false;
        if (coupon.getMaxRedemptions() != null && coupon.getRedeemedCount() >= coupon.getMaxRedemptions()) return false;
        return true;
    }

    public boolean appliesToOffering(Coupon coupon, String offeringId) {
        if (coupon.getAppliesToOfferingIds() == null || coupon.getAppliesToOfferingIds().isEmpty()) return true;
        return coupon.getAppliesToOfferingIds().contains(offeringId);
    }

    /**
     * Whether the coupon should apply on the given invoice cycle. ONCE applies only on the
     * first (NEW_PURCHASE) invoice; FOREVER on every cycle; LIMITED_PERIODS while
     * {@code redeemedCount < durationInPeriods}.
     */
    public boolean appliesOnInvoice(Coupon coupon, InvoiceType invoiceType) {
        return switch (coupon.getDuration()) {
            case ONCE -> invoiceType == InvoiceType.NEW_PURCHASE;
            case FOREVER -> true;
            case LIMITED_PERIODS -> coupon.getDurationInPeriods() != null
                    && coupon.getRedeemedCount() < coupon.getDurationInPeriods();
        };
    }

    /** Apply coupon to a subtotal. Returns the discounted Money (never below zero). */
    public Money apply(Coupon coupon, Money subtotal) {
        if (coupon == null) return subtotal;
        if (coupon.getMinOrderAmount() != null
                && subtotal.getAmount().compareTo(coupon.getMinOrderAmount().getAmount()) < 0) {
            return subtotal;
        }
        BigDecimal discounted;
        if (coupon.getType() == CouponType.PERCENT) {
            BigDecimal pct = coupon.getPercent() == null ? BigDecimal.ZERO : coupon.getPercent();
            discounted = subtotal.getAmount().multiply(BigDecimal.ONE.subtract(pct));
        } else if (coupon.getType() == CouponType.FIXED_AMOUNT) {
            BigDecimal flat = coupon.getFixedAmount() == null ? BigDecimal.ZERO : coupon.getFixedAmount().getAmount();
            discounted = subtotal.getAmount().subtract(flat);
        } else {
            discounted = subtotal.getAmount();
        }
        if (discounted.signum() < 0) discounted = BigDecimal.ZERO;
        return new Money(subtotal.getCurrency(), discounted.setScale(2, RoundingMode.HALF_UP));
    }

    public void recordRedemption(Coupon coupon) {
        coupon.setRedeemedCount(coupon.getRedeemedCount() + 1);
        repository.save(coupon);
    }
}
