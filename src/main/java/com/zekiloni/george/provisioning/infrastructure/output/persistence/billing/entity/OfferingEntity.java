package com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.provisioning.domain.billing.model.OfferingStatus;
import com.zekiloni.george.provisioning.domain.billing.model.OfferingType;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "offerings")
public class OfferingEntity extends BaseEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "identifier")
    private String identifier;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private OfferingType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OfferingStatus status;

    @JoinColumn(name = "offering_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferingCharacteristicEntity> characteristics;

    @JoinColumn(name = "offering_id")
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OfferingPriceEntity> pricing;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private DiscountEntity discount;

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;
}

