package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.domain.catalog.model.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OfferingDto {
    private String id;
    private String name;
    private String description;
    private String identifier;
    private OfferingType type;
    private OfferingStatus status;
    private BillingConfigDto billingConfig;
    private List<OfferingPriceDto> pricing;
    private Money price;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
