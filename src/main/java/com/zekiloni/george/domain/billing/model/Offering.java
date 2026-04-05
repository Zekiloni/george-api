package com.zekiloni.george.domain.billing.model;

import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.OfferingEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link OfferingEntity}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Offering {
    private String id;
    private String name;
    private OfferingType type;
    private OfferingStatus status;
    private String description;
    private String identifier;
    private List<OfferingCharacteristic> characteristics;
    private List<OfferingPrice> pricing;
    private Discount discount;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Money getMonthlyPrice() {
        return pricing.stream()
                .filter(price -> price.getPeriod() == BillingPeriod.MONTHLY)
                .findFirst()
                .map(OfferingPrice::getPrice)
                .orElse(null);
    }
}

