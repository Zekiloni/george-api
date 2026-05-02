package com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.TierMode;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.specification.characteristic.CharacteristicSpecificationEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Entity
@Table(name = "offerings")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class OfferingEntity extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "identifier", nullable = false, unique = true)
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferingStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_specification", nullable = false)
    private ServiceSpecification serviceSpecification;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "unit_amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "unit_currency", nullable = false))
    })
    @Embedded
    private Money unitAmount;

    @Column(name = "unit_label")
    private String unitLabel;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_unit")
    private ChronoUnit timeUnit;

    @Column(name = "interval_count", nullable = false)
    private int intervalCount;

    @Column(name = "min_units")
    private Integer minUnits;

    @Column(name = "max_units")
    private Integer maxUnits;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier_mode", nullable = false)
    private TierMode tierMode;

    @Column(name = "renewal_notice_days")
    private Integer renewalNoticeDays;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private List<DiscountTierEntity> tiers;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private List<CharacteristicSpecificationEntity> characteristicSpecification;
}
