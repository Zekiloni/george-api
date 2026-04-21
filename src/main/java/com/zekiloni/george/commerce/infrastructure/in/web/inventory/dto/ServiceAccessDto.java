package com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto;

import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAccessDto {
    private String id;
    private ServiceSpecification serviceSpecification;
    private String orderItemId;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private ServiceStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String tenantId;
}

