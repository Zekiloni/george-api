package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.application.port.out.promotion.CouponRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceType;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.domain.promotion.model.CouponDuration;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import com.zekiloni.george.commerce.domain.promotion.model.CouponType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponServiceTest {

    @Mock
    private CouponRepositoryPort repository;

    private CouponService service;

    @BeforeEach
    void setUp() {
        service = new CouponService(repository);
    }

    // --- isUsable / findApplicable ---

    @Nested
    class IsUsable {

        @Test
        void activeWithinWindowAndUnderCapIsUsable() {
            Coupon c = baseCoupon().status(CouponStatus.ACTIVE).build();
            assertThat(service.isUsable(c)).isTrue();
        }

        @Test
        void inactiveIsNotUsable() {
            Coupon c = baseCoupon().status(CouponStatus.INACTIVE).build();
            assertThat(service.isUsable(c)).isFalse();
        }

        @Test
        void archivedIsNotUsable() {
            Coupon c = baseCoupon().status(CouponStatus.ARCHIVED).build();
            assertThat(service.isUsable(c)).isFalse();
        }

        @Test
        void beforeValidFromIsNotUsable() {
            Coupon c = baseCoupon()
                    .status(CouponStatus.ACTIVE)
                    .validFrom(OffsetDateTime.now().plusDays(1))
                    .build();
            assertThat(service.isUsable(c)).isFalse();
        }

        @Test
        void afterValidToIsNotUsable() {
            Coupon c = baseCoupon()
                    .status(CouponStatus.ACTIVE)
                    .validTo(OffsetDateTime.now().minusSeconds(1))
                    .build();
            assertThat(service.isUsable(c)).isFalse();
        }

        @Test
        void redeemedAtCapIsNotUsable() {
            Coupon c = baseCoupon()
                    .status(CouponStatus.ACTIVE)
                    .maxRedemptions(10)
                    .redeemedCount(10)
                    .build();
            assertThat(service.isUsable(c)).isFalse();
        }

        @Test
        void nullIsNotUsable() {
            assertThat(service.isUsable(null)).isFalse();
        }
    }

    @Nested
    class FindApplicable {

        @Test
        void delegatesAndFiltersByUsable() {
            Coupon usable = baseCoupon().status(CouponStatus.ACTIVE).build();
            when(repository.findByCode("OK")).thenReturn(Optional.of(usable));

            Optional<Coupon> result = service.findApplicable("OK");

            assertThat(result).contains(usable);
        }

        @Test
        void filtersOutUnusable() {
            Coupon archived = baseCoupon().status(CouponStatus.ARCHIVED).build();
            when(repository.findByCode("BAD")).thenReturn(Optional.of(archived));

            assertThat(service.findApplicable("BAD")).isEmpty();
        }
    }

    // --- appliesToOffering ---

    @Nested
    class AppliesToOffering {

        @Test
        void emptyScopeMatchesAll() {
            Coupon c = baseCoupon().appliesToOfferingIds(null).build();
            assertThat(service.appliesToOffering(c, "any")).isTrue();
        }

        @Test
        void emptyListMatchesAll() {
            Coupon c = baseCoupon().appliesToOfferingIds(List.of()).build();
            assertThat(service.appliesToOffering(c, "any")).isTrue();
        }

        @Test
        void scopedMatch() {
            Coupon c = baseCoupon().appliesToOfferingIds(List.of("a", "b")).build();
            assertThat(service.appliesToOffering(c, "a")).isTrue();
        }

        @Test
        void scopedNoMatch() {
            Coupon c = baseCoupon().appliesToOfferingIds(List.of("a", "b")).build();
            assertThat(service.appliesToOffering(c, "c")).isFalse();
        }
    }

    // --- appliesOnInvoice ---

    @Nested
    class AppliesOnInvoice {

        @Test
        void onceAppliesOnNewPurchase() {
            Coupon c = baseCoupon().duration(CouponDuration.ONCE).build();
            assertThat(service.appliesOnInvoice(c, InvoiceType.NEW_PURCHASE)).isTrue();
        }

        @Test
        void onceSkipsRenewal() {
            Coupon c = baseCoupon().duration(CouponDuration.ONCE).build();
            assertThat(service.appliesOnInvoice(c, InvoiceType.RENEWAL)).isFalse();
        }

        @Test
        void foreverAppliesAlways() {
            Coupon c = baseCoupon().duration(CouponDuration.FOREVER).build();
            assertThat(service.appliesOnInvoice(c, InvoiceType.NEW_PURCHASE)).isTrue();
            assertThat(service.appliesOnInvoice(c, InvoiceType.RENEWAL)).isTrue();
        }

        @Test
        void limitedPeriodsAppliesWhileRemaining() {
            Coupon c = baseCoupon()
                    .duration(CouponDuration.LIMITED_PERIODS)
                    .durationInPeriods(3)
                    .redeemedCount(2)
                    .build();
            assertThat(service.appliesOnInvoice(c, InvoiceType.RENEWAL)).isTrue();
        }

        @Test
        void limitedPeriodsExhausted() {
            Coupon c = baseCoupon()
                    .duration(CouponDuration.LIMITED_PERIODS)
                    .durationInPeriods(3)
                    .redeemedCount(3)
                    .build();
            assertThat(service.appliesOnInvoice(c, InvoiceType.RENEWAL)).isFalse();
        }

        @Test
        void limitedPeriodsWithoutCountConfiguredIsFalse() {
            Coupon c = baseCoupon()
                    .duration(CouponDuration.LIMITED_PERIODS)
                    .durationInPeriods(null)
                    .build();
            assertThat(service.appliesOnInvoice(c, InvoiceType.NEW_PURCHASE)).isFalse();
        }
    }

    // --- apply ---

    @Nested
    class Apply {

        @Test
        void percentDiscount() {
            Coupon c = baseCoupon().type(CouponType.PERCENT).percent(new BigDecimal("0.20")).build();
            Money out = service.apply(c, Money.of("USD", new BigDecimal("100.00")));
            assertThat(out.getAmount()).isEqualByComparingTo("80.00");
        }

        @Test
        void fixedAmountDiscount() {
            Coupon c = baseCoupon()
                    .type(CouponType.FIXED_AMOUNT)
                    .fixedAmount(Money.of("USD", new BigDecimal("15.00")))
                    .build();
            Money out = service.apply(c, Money.of("USD", new BigDecimal("100.00")));
            assertThat(out.getAmount()).isEqualByComparingTo("85.00");
        }

        @Test
        void fixedAmountClampsAtZero() {
            Coupon c = baseCoupon()
                    .type(CouponType.FIXED_AMOUNT)
                    .fixedAmount(Money.of("USD", new BigDecimal("200.00")))
                    .build();
            Money out = service.apply(c, Money.of("USD", new BigDecimal("100.00")));
            assertThat(out.getAmount()).isEqualByComparingTo("0.00");
        }

        @Test
        void belowMinOrderAmountSkipsDiscount() {
            Coupon c = baseCoupon()
                    .type(CouponType.PERCENT)
                    .percent(new BigDecimal("0.50"))
                    .minOrderAmount(Money.of("USD", new BigDecimal("100.00")))
                    .build();
            Money out = service.apply(c, Money.of("USD", new BigDecimal("99.99")));
            assertThat(out.getAmount()).isEqualByComparingTo("99.99");
        }

        @Test
        void atOrAboveMinOrderAmountDiscounts() {
            Coupon c = baseCoupon()
                    .type(CouponType.PERCENT)
                    .percent(new BigDecimal("0.50"))
                    .minOrderAmount(Money.of("USD", new BigDecimal("100.00")))
                    .build();
            Money out = service.apply(c, Money.of("USD", new BigDecimal("100.00")));
            assertThat(out.getAmount()).isEqualByComparingTo("50.00");
        }

        @Test
        void nullCouponReturnsSubtotal() {
            Money subtotal = Money.of("USD", new BigDecimal("100.00"));
            assertThat(service.apply(null, subtotal)).isSameAs(subtotal);
        }
    }

    @Nested
    class RecordRedemption {
        @Test
        void incrementsAndPersists() {
            Coupon c = baseCoupon().redeemedCount(2).build();
            service.recordRedemption(c);

            assertThat(c.getRedeemedCount()).isEqualTo(3);
            verify(repository, times(1)).save(any(Coupon.class));
        }
    }

    private static Coupon.CouponBuilder baseCoupon() {
        return Coupon.builder()
                .id("c1")
                .code("CODE")
                .status(CouponStatus.ACTIVE)
                .type(CouponType.PERCENT)
                .duration(CouponDuration.ONCE)
                .percent(new BigDecimal("0.10"))
                .redeemedCount(0);
    }
}
