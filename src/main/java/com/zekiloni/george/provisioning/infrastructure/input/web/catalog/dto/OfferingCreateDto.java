package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.OfferingStatus;
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
    private List<OfferingCharacteristicCreateDto> characteristics;
    private BillingConfigCreateDto billingConfig;
    private List<OfferingPriceCreateDto> pricing;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

