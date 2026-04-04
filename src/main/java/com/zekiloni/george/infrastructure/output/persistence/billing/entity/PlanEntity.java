package com.zekiloni.george.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.infrastructure.output.persistence.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "plans")
public class PlanEntity extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "identifier")
    private String identifier;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanFeatureEntity> features;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PeriodPriceEntity> pricing;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_public", nullable = false)
    private boolean isPublic;
}