package com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import lombok.AllArgsConstructor;
import lombok.Builder;
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
    private String orderId;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String tenantId;
}

