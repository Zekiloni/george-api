package com.zekiloni.george.domain.billing.model;

import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.OfferingPriceEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for {@link OfferingPriceEntity}
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OfferingPrice{
    private String id;
    private BillingPeriod period;
    private Money price;
}