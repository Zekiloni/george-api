package com.zekiloni.george.provisioning.domain.catalog.model;

import com.zekiloni.george.common.domain.model.Money;
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
    private OfferingType type;
    private OfferingStatus status;
    private List<OfferingCharacteristic> characteristics;
    private BillingConfig billingConfig;
    private List<OfferingPrice> pricing;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Money getPrice() {
        if (pricing == null || pricing.isEmpty()) return null;

        return switch (billingConfig.getType()) {
            case ONE_TIME, USAGE_BASED -> pricing.get(0).getEffectiveUnitPrice();
            case RECURRING -> pricing.stream()
                    .min(Comparator.comparingInt(OfferingPrice::getDuration))
                    .map(OfferingPrice::getEffectiveUnitPrice)
                    .orElse(null);
        };
    }
}

