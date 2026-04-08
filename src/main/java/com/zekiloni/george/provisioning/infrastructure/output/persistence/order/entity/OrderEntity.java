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

import java.util.List;


@Entity
@Table(name = "orders")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderEntity extends BaseEntity {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    List<OrderItemEntity> item;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    private InvoiceEntity invoice;
}
