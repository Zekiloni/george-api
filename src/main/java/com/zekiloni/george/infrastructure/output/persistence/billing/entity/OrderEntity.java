package com.zekiloni.george.infrastructure.output.persistence.billing.entity;

import com.zekiloni.george.infrastructure.output.persistence.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity extends BaseEntity {
    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;

    @ManyToOne
    @JoinColumn(name = "offering_id", nullable = false)
    private OfferingEntity offering;
}

