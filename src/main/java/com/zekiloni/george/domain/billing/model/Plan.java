package com.zekiloni.george.domain.billing.model;

import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.PlanEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for {@link PlanEntity}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    private String id;
    private String name;
    private String description;
    private String identifier;
    private List<PlanFeature> features;
    private List<PeriodPrice> pricing;
    private boolean isActive;
    private boolean isPublic;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Money getMonthlyPrice() {
        return pricing.stream()
                .filter(price -> price.getPeriod() == BillingPeriod.MONTHLY)
                .findFirst()
                .map(a -> a.getPrice())
                .orElse(null);
    }
}
