package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.provisioning.domain.catalog.model.DurationUnit;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.order.model.OrderStatus;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity.OfferingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;


@Entity
@Table(name = "orders")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderEntity extends BaseEntity {
    @OneToOne(optional = false)
    @JoinColumn(name = "offering_id", referencedColumnName = "id")
    private OfferingEntity offering;

    @Column
    private Integer quantity;

    @Column
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit")
    private DurationUnit durationUnit;

    @Column(nullable = false)
    private OrderStatus status;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    private InvoiceEntity invoice;
}
