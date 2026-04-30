package com.zekiloni.george.commerce.domain.inventory.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
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
public abstract class ServiceAccess {
    private String id;
    private ServiceSpecification serviceSpecification;
    private OrderItem orderItem;
    private List<Characteristic> characteristic;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private ServiceStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String tenantId;
}
