package com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.CharacteristicEntity;
import com.zekiloni.george.commerce.domain.catalog.model.DurationUnit;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.OfferingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;


@Entity
@Table(name = "order_items")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderItemEntity extends BaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private OfferingEntity offering;

    @Column
    private Integer quantity;

    @Column
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration_unit")
    private DurationUnit durationUnit;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "invoice_id", referencedColumnName = "id")
    private InvoiceEntity invoice;

    @ManyToMany
    @JoinTable(
            name = "order_item_characteristics",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "characteristic_id")
    )
    private List<CharacteristicEntity> characteristics;
}
