package com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "discount_tiers")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DiscountTierEntity extends BaseEntity {

    @Column(name = "from_units", nullable = false)
    private Integer fromUnits;

    @Column(name = "discount", nullable = false, precision = 5, scale = 4)
    private BigDecimal discount;
}
