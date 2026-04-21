package com.zekiloni.george.provisioning.infrastructure.in.web.catalog.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.infrastructure.in.web.catalog.dto.specification.characteristic.CharacteristicSpecificationCreateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferingCreateDto {
    private String name;
    private String description;
    private String identifier;
    private OfferingStatus status;
    private ServiceSpecification serviceSpecification;
    private BillingConfigCreateDto billingConfig;
    private List<CharacteristicSpecificationCreateDto> characteristicSpecification;
    private List<OfferingPriceCreateDto> pricing;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

