package com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.provisioning.domain.billing.model.BillingPeriod;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "period_prices")
public class OfferingPriceEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "period")
    private BillingPeriod period;

    @Embedded
    private Money price;
}