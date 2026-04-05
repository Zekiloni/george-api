package com.zekiloni.george.provisioning.domain.billing.model;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.entity.OfferingPriceEntity;
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