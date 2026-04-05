package com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.provisioning.domain.billing.model.BillingPeriod;
import com.zekiloni.george.provisioning.domain.billing.model.DiscountType;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.TemporalEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Entity
@Table
public class DiscountEntity extends TemporalEntity {
    private String code;
    private String description;

    private DiscountType type;

    private BigDecimal value;

    private Set<BillingPeriod> applicablePeriods;
}