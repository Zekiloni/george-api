package com.zekiloni.george.provisioning.domain.inventory.model;

import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Getter
@Builder
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
