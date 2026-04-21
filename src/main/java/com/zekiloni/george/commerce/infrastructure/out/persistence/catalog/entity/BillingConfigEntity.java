package com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.commerce.domain.catalog.model.DurationUnit;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "billing_configs")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class BillingConfigEntity extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OfferingType type;

    @Column(name = "quantity_allowed", nullable = false)
    private Boolean quantityAllowed;

    @Column(name = "max_quantity")
    private Integer maxQuantity;

    @Column(name = "duration_allowed", nullable = false)
    private Boolean durationAllowed;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit")
    private DurationUnit durationUnit;
}

