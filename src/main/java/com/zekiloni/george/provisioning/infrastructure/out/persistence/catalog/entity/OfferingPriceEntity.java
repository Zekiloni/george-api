package com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.entity;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.provisioning.domain.catalog.model.DurationUnit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "offering_prices")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OfferingPriceEntity extends BaseEntity {
    @Column(name = "label", nullable = false)
    private String label;

    @Embedded
    private Money unitPrice;

    @Column(name = "duration")
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit")
    private DurationUnit durationUnit;

    @Column(name = "discount")
    private BigDecimal discount;
}

