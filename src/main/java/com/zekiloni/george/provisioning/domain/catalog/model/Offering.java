package com.zekiloni.george.provisioning.domain.catalog.model;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;


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
    private List<CharacteristicSpecification> characteristicSpecification;
    private OfferingStatus status;
    private BillingConfig billingConfig;
    private List<OfferingPrice> pricing;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Money getPrice() {
        if (pricing == null || pricing.isEmpty()) return null;

        return switch (billingConfig.getType()) {
            case ONE_TIME, USAGE_BASED -> pricing.get(0).getEffectiveUnitPrice();
            case RECURRING -> pricing.stream()
                    .filter(p -> p.getDuration() != null)
                    .min(Comparator.comparingInt(OfferingPrice::getDuration))
                    .map(OfferingPrice::getEffectiveUnitPrice)
                    .orElse(null);
        };
    }

    public Money getPrice(int duration, DurationUnit durationUnit) {
        if (pricing == null || pricing.isEmpty()) return null;

        return switch (billingConfig.getType()) {
            case ONE_TIME, USAGE_BASED -> pricing.get(0).getEffectiveUnitPrice();
            case RECURRING -> {
                OfferingPrice exactMatch = pricing.stream()
                        .filter(p -> p.getDuration() != null
                                && p.getDuration() == duration
                                && p.getDurationUnit() == durationUnit)
                        .findFirst()
                        .orElse(null);

                if (exactMatch != null) {
                    yield exactMatch.getEffectiveUnitPrice();
                }

                OfferingPrice baseTier = pricing.stream()
                        .filter(p -> p.getDuration() != null && p.getDurationUnit() == durationUnit)
                        .min(Comparator.comparingInt(OfferingPrice::getDuration))
                        .orElseThrow(() -> new IllegalArgumentException(
                                "No pricing found for unit: " + durationUnit));

                int multiplier = duration / baseTier.getDuration();
                yield baseTier.getEffectiveUnitPrice().multiply(multiplier);
            }
        };
    }
}

