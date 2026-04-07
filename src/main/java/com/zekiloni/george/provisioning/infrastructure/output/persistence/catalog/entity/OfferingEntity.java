package com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity.specification.characteristic.CharacteristicSpecificationEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private List<CharacteristicSpecificationEntity> characteristicSpecification;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_config_id")
    private BillingConfigEntity billingConfig;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id")
    private List<OfferingPriceEntity> pricing;
}

