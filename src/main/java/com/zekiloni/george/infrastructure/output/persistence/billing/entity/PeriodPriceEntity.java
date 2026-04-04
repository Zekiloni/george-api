package com.zekiloni.george.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.domain.billing.model.BillingPeriod;
import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.output.persistence.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "period_prices")
public class PeriodPriceEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(name = "period")
    private BillingPeriod period;

    @Embedded
    private Money price;

    @ManyToOne
    @JoinColumn(name = "plan_id")
    private PlanEntity plan;
}