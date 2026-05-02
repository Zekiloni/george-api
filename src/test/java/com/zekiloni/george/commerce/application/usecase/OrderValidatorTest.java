package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.commerce.domain.catalog.model.TierMode;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.PriceImpact;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderValidatorTest {

    @Mock
    private OfferingQueryUseCase offeringQueryUseCase;
    @Mock
    private CouponService couponService;

    private OrderValidator validator;

    @BeforeEach
    void setUp() {
        validator = new OrderValidator(offeringQueryUseCase, couponService);
    }

    @Nested
    class StructuralChecks {

        @Test
        void emptyItemsThrows() {
            Order order = Order.builder().item(List.of()).build();
            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class);
        }

        @Test
        void nullItemsThrows() {
            Order order = Order.builder().build();
            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class);
        }

        @Test
        void itemMissingOfferingThrows() {
            Order order = Order.builder()
                    .item(List.of(OrderItem.builder().units(1).build()))
                    .build();
            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("missing an offering");
        }

        @Test
        void unknownOfferingThrows() {
            when(offeringQueryUseCase.getById("missing")).thenReturn(Optional.empty());
            Order order = orderWith(itemRef("missing", 1));

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("Offering not found");
        }
    }

    @Nested
    class OfferingState {

        @Test
        void inactiveOfferingThrows() {
            Offering offering = activeOffering("o1");
            offering.setStatus(OfferingStatus.INACTIVE);
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            Order order = orderWith(itemRef("o1", 1));

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("not available");
        }

        @Test
        void activeOfferingPasses() {
            Offering offering = activeOffering("o1");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            Order order = orderWith(itemRef("o1", 1));

            assertThatCode(() -> validator.validate(order)).doesNotThrowAnyException();
        }
    }

    @Nested
    class UnitsBounds {

        @Test
        void unitsZeroThrows() {
            Offering offering = activeOffering("o1");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            Order order = orderWith(itemRef("o1", 0));

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class);
        }

        @Test
        void unitsBelowMinThrows() {
            Offering offering = activeOffering("o1");
            offering.setMinUnits(5);
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            Order order = orderWith(itemRef("o1", 3));

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("Minimum");
        }

        @Test
        void unitsAboveMaxThrows() {
            Offering offering = activeOffering("o1");
            offering.setMaxUnits(10);
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            Order order = orderWith(itemRef("o1", 11));

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("Maximum");
        }
    }

    @Nested
    class CharacteristicValidation {

        @Test
        void requiredCharacteristicMissingThrows() {
            Offering offering = activeOffering("o1");
            offering.setCharacteristicSpecification(List.of(
                    CharacteristicSpecification.builder()
                            .name("region")
                            .valueType("string")
                            .minCardinality(1)
                            .priceImpact(PriceImpact.NONE)
                            .build()
            ));
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            Order order = orderWith(itemRef("o1", 1));

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("required");
        }

        @Test
        void unknownCharacteristicThrows() {
            Offering offering = activeOffering("o1");
            offering.setCharacteristicSpecification(List.of());
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            OrderItem item = OrderItem.builder()
                    .offering(Offering.builder().id("o1").build())
                    .units(1)
                    .characteristic(List.of(
                            Characteristic.builder().name("ghost").value("x").build()
                    ))
                    .build();

            assertThatThrownBy(() -> validator.validate(Order.builder().item(List.of(item)).build()))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("Unknown characteristic");
        }

        @Test
        void valueOutsideAllowedSetThrows() {
            Offering offering = activeOffering("o1");
            offering.setCharacteristicSpecification(List.of(
                    CharacteristicSpecification.builder()
                            .name("size")
                            .valueType("string")
                            .characteristicValueSpecification(List.of(
                                    CharacteristicValueSpecification.builder().value("S").build(),
                                    CharacteristicValueSpecification.builder().value("M").build()
                            ))
                            .priceImpact(PriceImpact.NONE)
                            .build()
            ));
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            OrderItem item = OrderItem.builder()
                    .offering(Offering.builder().id("o1").build())
                    .units(1)
                    .characteristic(List.of(
                            Characteristic.builder().name("size").value("XL").build()
                    ))
                    .build();

            assertThatThrownBy(() -> validator.validate(Order.builder().item(List.of(item)).build()))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("not allowed");
        }

        @Test
        void regexFailureThrows() {
            Offering offering = activeOffering("o1");
            offering.setCharacteristicSpecification(List.of(
                    CharacteristicSpecification.builder()
                            .name("phone")
                            .valueType("string")
                            .regex("^\\d{3}$")
                            .priceImpact(PriceImpact.NONE)
                            .build()
            ));
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            OrderItem item = OrderItem.builder()
                    .offering(Offering.builder().id("o1").build())
                    .units(1)
                    .characteristic(List.of(
                            Characteristic.builder().name("phone").value("ABC").build()
                    ))
                    .build();

            assertThatThrownBy(() -> validator.validate(Order.builder().item(List.of(item)).build()))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("pattern");
        }

        @Test
        void allowedValueAndRegexMatchPasses() {
            Offering offering = activeOffering("o1");
            offering.setCharacteristicSpecification(List.of(
                    CharacteristicSpecification.builder()
                            .name("size")
                            .valueType("string")
                            .characteristicValueSpecification(List.of(
                                    CharacteristicValueSpecification.builder().value("M").build()
                            ))
                            .priceImpact(PriceImpact.NONE)
                            .build()
            ));
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            OrderItem item = OrderItem.builder()
                    .offering(Offering.builder().id("o1").build())
                    .units(1)
                    .characteristic(List.of(
                            Characteristic.builder().name("size").value("M").build()
                    ))
                    .build();

            assertThatCode(() -> validator.validate(Order.builder().item(List.of(item)).build()))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    class CouponValidation {

        @Test
        void invalidCouponCodeThrows() {
            Offering offering = activeOffering("o1");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));
            when(couponService.findApplicable("BAD")).thenReturn(Optional.empty());

            Order order = orderWith(itemRef("o1", 1));
            order.setCouponCode("BAD");

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("invalid or expired");
        }

        @Test
        void couponDoesNotApplyToAnyItemThrows() {
            Offering offering = activeOffering("o1");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            Coupon coupon = Coupon.builder()
                    .code("OTHER")
                    .type(CouponType.PERCENT)
                    .duration(CouponDuration.ONCE)
                    .percent(new BigDecimal("0.10"))
                    .status(CouponStatus.ACTIVE)
                    .appliesToOfferingIds(List.of("other-offering"))
                    .build();
            when(couponService.findApplicable("OTHER")).thenReturn(Optional.of(coupon));
            when(couponService.appliesToOffering(coupon, "o1")).thenReturn(false);

            Order order = orderWith(itemRef("o1", 1));
            order.setCouponCode("OTHER");

            assertThatThrownBy(() -> validator.validate(order))
                    .isInstanceOf(OrderValidationException.class)
                    .hasMessageContaining("does not apply");
        }

        @Test
        void couponAppliesToAtLeastOneItemPasses() {
            Offering offering = activeOffering("o1");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            Coupon coupon = Coupon.builder()
                    .code("OK")
                    .type(CouponType.PERCENT)
                    .duration(CouponDuration.ONCE)
                    .percent(new BigDecimal("0.10"))
                    .status(CouponStatus.ACTIVE)
                    .build();
            when(couponService.findApplicable("OK")).thenReturn(Optional.of(coupon));
            lenient().when(couponService.appliesToOffering(coupon, "o1")).thenReturn(true);

            Order order = orderWith(itemRef("o1", 1));
            order.setCouponCode("OK");

            assertThatCode(() -> validator.validate(order)).doesNotThrowAnyException();
        }

        @Test
        void noCouponCodeNoCouponLookup() {
            Offering offering = activeOffering("o1");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(offering));

            Order order = orderWith(itemRef("o1", 1));

            assertThatCode(() -> validator.validate(order)).doesNotThrowAnyException();
            // couponService.findApplicable is never called — no `when` stub needed
        }
    }

    @Nested
    class SideEffects {

        @Test
        void resolvesAndSetsCanonicalOffering() {
            Offering canonical = activeOffering("o1");
            canonical.setName("Canonical");
            when(offeringQueryUseCase.getById("o1")).thenReturn(Optional.of(canonical));

            OrderItem item = itemRef("o1", 1);
            Order order = Order.builder().item(List.of(item)).build();

            validator.validate(order);

            assertThat(item.getOffering()).isSameAs(canonical);
            assertThat(item.getOffering().getName()).isEqualTo("Canonical");
        }
    }

    // --- helpers ---

    private static Offering activeOffering(String id) {
        return Offering.builder()
                .id(id)
                .name("Plan-" + id)
                .status(OfferingStatus.ACTIVE)
                .unitAmount(Money.of("USD", new BigDecimal("20.00")))
                .timeUnit(ChronoUnit.MONTHS)
                .intervalCount(1)
                .minUnits(1)
                .tierMode(TierMode.NONE)
                .build();
    }

    private static OrderItem itemRef(String offeringId, int units) {
        return OrderItem.builder()
                .offering(Offering.builder().id(offeringId).build())
                .units(units)
                .build();
    }

    private static Order orderWith(OrderItem... items) {
        return Order.builder().item(List.of(items)).build();
    }
}
