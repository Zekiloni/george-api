package com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.entity.specification.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Entity
@Table(name = "characteristic_specifications")
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CharacteristicSpecificationEntity extends BaseEntity {
    @Column(name = "name", nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String valueType;

    @Column
    private Boolean configurable;

    @Column
    private Boolean extensible;

    @Column
    private Boolean isUnique;

    @Column
    private Integer maxCardinality;

    @Column
    private Integer minCardinality;

    @Column
    private String regex;

    @Embedded
    private TimePeriod validFor;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "characteristic_specification_id")
    private List<CharacteristicValueSpecificationEntity> characteristicValueSpecification;
}