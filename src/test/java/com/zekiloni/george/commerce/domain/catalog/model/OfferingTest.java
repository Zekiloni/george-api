package com.zekiloni.george.commerce.domain.catalog.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.domain.catalog.exception.OfferingPricingException;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.PriceImpact;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceType;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferingTest {

    @Nested
    class PriceFor {

        @Test
        void simpleUnitsTimesPrice() {
            // SMTP weekly $30 × 1 = $30
            Offering offering = recurringOffering("SMTP weekly", "30.00", ChronoUnit.WEEKS);
            OrderItem item = OrderItem.builder().offering(offering).units(1).build();

            Money total = offering.priceFor(item);

            assertThat(total.getAmount()).isEqualByComparingTo("30.00");
            assertThat(total.getCurrency().getCurrencyCode()).isEqualTo("USD");
        }

        @Test
        void multiplyByUnits() {
            // LEADS $5 × 50 = $250
            Offering offering = quotaOffering("Leads", "5.00", "lead");
            OrderItem item = OrderItem.builder().offering(offering).units(50).build();

            assertThat(offering.priceFor(item).getAmount()).isEqualByComparingTo("250.00");
        }

        @Test
        void volumeTierAppliedToWholeSubtotal() {
            // SMTP monthly $20 × 12 with tier {12, 20%} = $20 × 12 × 0.80 = $192
            Offering offering = recurringOffering("SMTP monthly", "20.00", ChronoUnit.MONTHS);
            offering.setTierMode(TierMode.VOLUME);
            offering.setTiers(List.of(tier(12, "0.20")));
            OrderItem item = OrderItem.builder().offering(offering).units(12).build();

            assertThat(offering.priceFor(item).getAmount()).isEqualByComparingTo("192.00");
        }

        @Test
        void volumeTierBelowThresholdHasNoEffect() {
            Offering offering = recurringOffering("SMTP monthly", "20.00", ChronoUnit.MONTHS);
            offering.setTierMode(TierMode.VOLUME);
            offering.setTiers(List.of(tier(12, "0.20")));
            OrderItem item = OrderItem.builder().offering(offering).units(11).build();

            assertThat(offering.priceFor(item).getAmount()).isEqualByComparingTo("220.00");
        }

        @Test
        void deepestMatchingVolumeTierWins() {
            // {6, 10%}, {12, 20%} on 12 units → 20% off
            Offering offering = recurringOffering("SMTP monthly", "20.00", ChronoUnit.MONTHS);
            offering.setTierMode(TierMode.VOLUME);
            offering.setTiers(List.of(tier(6, "0.10"), tier(12, "0.20")));
            OrderItem item = OrderItem.builder().offering(offering).units(12).build();

            assertThat(offering.priceFor(item).getAmount()).isEqualByComparingTo("192.00");
        }

        @Test
        void tierModeNoneIgnoresTiers() {
            // Tiers defined but mode=NONE → no discount
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            offering.setTierMode(TierMode.NONE);
            offering.setTiers(List.of(tier(1, "0.50")));
            OrderItem item = OrderItem.builder().offering(offering).units(12).build();

            assertThat(offering.priceFor(item).getAmount()).isEqualByComparingTo("240.00");
        }

        @Test
        void recurringCharacteristicAddedOnNewPurchase() {
            // GSM: $20 × 6 + dedicated_port (RECURRING +$10) = $130
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("dedicated_port", PriceImpact.RECURRING, "10.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(charValue("dedicated_port", true)))
                    .build();

            assertThat(offering.priceFor(item, InvoiceType.NEW_PURCHASE).getAmount())
                    .isEqualByComparingTo("130.00");
        }

        @Test
        void recurringCharacteristicAlsoAppliedOnRenewal() {
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("dedicated_port", PriceImpact.RECURRING, "10.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(charValue("dedicated_port", true)))
                    .build();

            assertThat(offering.priceFor(item, InvoiceType.RENEWAL).getAmount())
                    .isEqualByComparingTo("130.00");
        }

        @Test
        void oneTimeCharacteristicAppliedOnNewPurchase() {
            // Setup fee +$10 ONE_TIME
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("setup", PriceImpact.ONE_TIME, "10.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(charValue("setup", true)))
                    .build();

            assertThat(offering.priceFor(item, InvoiceType.NEW_PURCHASE).getAmount())
                    .isEqualByComparingTo("130.00");
        }

        @Test
        void oneTimeCharacteristicSkippedOnRenewal() {
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("setup", PriceImpact.ONE_TIME, "10.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(charValue("setup", true)))
                    .build();

            assertThat(offering.priceFor(item, InvoiceType.RENEWAL).getAmount())
                    .isEqualByComparingTo("120.00");
        }

        @Test
        void noneImpactCharacteristicIgnored() {
            // Region tag — has priceAdjustment but priceImpact=NONE → no effect
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("region", PriceImpact.NONE, "10.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(charValue("region", true)))
                    .build();

            assertThat(offering.priceFor(item).getAmount()).isEqualByComparingTo("120.00");
        }

        @Test
        void multipleCharacteristicsSummed() {
            // $20 × 6 + dedicated_port +$10 RECURRING + priority +$5 RECURRING = $135
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("dedicated_port", PriceImpact.RECURRING, "10.00"),
                    booleanCharSpec("priority", PriceImpact.RECURRING, "5.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(
                            charValue("dedicated_port", true),
                            charValue("priority", true)
                    ))
                    .build();

            assertThat(offering.priceFor(item, InvoiceType.NEW_PURCHASE).getAmount())
                    .isEqualByComparingTo("135.00");
        }

        @Test
        void volumeDiscountAppliesToUnitsButNotCharAdjustments() {
            // $20 × 6 × 0.90 + setup ONE_TIME +$10 = $108 + $10 = $118
            Offering offering = recurringOffering("GSM", "20.00", ChronoUnit.MONTHS);
            offering.setTierMode(TierMode.VOLUME);
            offering.setTiers(List.of(tier(6, "0.10")));
            offering.setCharacteristicSpecification(List.of(
                    booleanCharSpec("setup", PriceImpact.ONE_TIME, "10.00")
            ));
            OrderItem item = OrderItem.builder()
                    .offering(offering)
                    .units(6)
                    .characteristic(List.of(charValue("setup", true)))
                    .build();

            assertThat(offering.priceFor(item, InvoiceType.NEW_PURCHASE).getAmount())
                    .isEqualByComparingTo("118.00");
        }

        @Test
        void unitsNullThrows() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            OrderItem item = OrderItem.builder().offering(offering).build();

            assertThatThrownBy(() -> offering.priceFor(item))
                    .isInstanceOf(OfferingPricingException.class)
                    .hasMessageContaining("units > 0");
        }

        @Test
        void unitsZeroThrows() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            OrderItem item = OrderItem.builder().offering(offering).units(0).build();

            assertThatThrownBy(() -> offering.priceFor(item))
                    .isInstanceOf(OfferingPricingException.class);
        }

        @Test
        void unitsBelowMinThrows() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            offering.setMinUnits(3);
            OrderItem item = OrderItem.builder().offering(offering).units(2).build();

            assertThatThrownBy(() -> offering.priceFor(item))
                    .isInstanceOf(OfferingPricingException.class)
                    .hasMessageContaining("Minimum");
        }

        @Test
        void unitsAboveMaxThrows() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            offering.setMaxUnits(10);
            OrderItem item = OrderItem.builder().offering(offering).units(11).build();

            assertThatThrownBy(() -> offering.priceFor(item))
                    .isInstanceOf(OfferingPricingException.class)
                    .hasMessageContaining("Maximum");
        }

        @Test
        void noUnitAmountThrows() {
            Offering offering = Offering.builder()
                    .id("o1").name("Broken")
                    .build();
            OrderItem item = OrderItem.builder().offering(offering).units(1).build();

            assertThatThrownBy(() -> offering.priceFor(item))
                    .isInstanceOf(OfferingPricingException.class)
                    .hasMessageContaining("no unit amount");
        }
    }

    @Nested
    class StartingPrice {

        @Test
        void noTiersReturnsUnitAmount() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            assertThat(offering.getStartingPrice().getAmount()).isEqualByComparingTo("20.00");
        }

        @Test
        void withVolumeTiersReturnsDeepestDiscountedPrice() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            offering.setTiers(List.of(tier(6, "0.10"), tier(12, "0.25")));
            // 20 × (1 - 0.25) = 15
            assertThat(offering.getStartingPrice().getAmount()).isEqualByComparingTo("15.00");
        }

        @Test
        void emptyTiersReturnsUnitAmount() {
            Offering offering = recurringOffering("Plan", "20.00", ChronoUnit.MONTHS);
            offering.setTiers(List.of());
            assertThat(offering.getStartingPrice().getAmount()).isEqualByComparingTo("20.00");
        }
    }

    // --- helpers ---

    private static Offering recurringOffering(String name, String amount, ChronoUnit unit) {
        return Offering.builder()
                .id("off-" + name)
                .name(name)
                .unitAmount(Money.of("USD", new BigDecimal(amount)))
                .unitLabel(unit.name().toLowerCase())
                .timeUnit(unit)
                .intervalCount(1)
                .minUnits(1)
                .tierMode(TierMode.NONE)
                .build();
    }

    private static Offering quotaOffering(String name, String amount, String unitLabel) {
        return Offering.builder()
                .id("off-" + name)
                .name(name)
                .unitAmount(Money.of("USD", new BigDecimal(amount)))
                .unitLabel(unitLabel)
                .timeUnit(null)
                .intervalCount(1)
                .minUnits(1)
                .tierMode(TierMode.NONE)
                .build();
    }

    private static DiscountTier tier(int fromUnits, String discount) {
        return new DiscountTier(null, fromUnits, new BigDecimal(discount));
    }

    private static CharacteristicSpecification booleanCharSpec(String name, PriceImpact impact, String adjustment) {
        return CharacteristicSpecification.builder()
                .name(name)
                .valueType("boolean")
                .priceImpact(impact)
                .characteristicValueSpecification(List.of(
                        CharacteristicValueSpecification.builder()
                                .value(true)
                                .priceAdjustment(Money.of("USD", new BigDecimal(adjustment)))
                                .build(),
                        CharacteristicValueSpecification.builder()
                                .value(false)
                                .priceAdjustment(Money.of("USD", BigDecimal.ZERO))
                                .build()
                ))
                .build();
    }

    private static Characteristic charValue(String name, Object value) {
        return Characteristic.builder().name(name).value(value).build();
    }
}
