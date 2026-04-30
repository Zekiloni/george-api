package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity;


import com.zekiloni.george.common.infrastructure.out.persistence.entity.CharacteristicEntity;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.OrderItemEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_access")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ServiceAccessEntity extends TenantEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "service_specification", nullable = false)
    private ServiceSpecification serviceSpecification;

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ServiceStatus status;

    @ManyToMany
    @JoinTable(
            name = "service_access_characteristics",
            joinColumns = @JoinColumn(name = "service_access_id"),
            inverseJoinColumns = @JoinColumn(name = "characteristic_id")
    )
    private List<CharacteristicEntity> characteristic;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false, unique = true)
    private OrderItemEntity orderItem;

    @Column(name = "gateway_id")
    private String gatewayId;
}
