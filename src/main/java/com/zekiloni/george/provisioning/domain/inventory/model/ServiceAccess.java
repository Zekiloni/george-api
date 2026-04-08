package com.zekiloni.george.provisioning.domain.inventory.model;

import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@SuperBuilder
@NoArgsConstructor
public abstract class ServiceAccess {
    private String id;
    private ServiceSpecification serviceSpecification;
    private Order order;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
