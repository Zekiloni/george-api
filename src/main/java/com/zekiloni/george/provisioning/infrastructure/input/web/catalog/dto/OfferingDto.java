package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.*;

import java.time.OffsetDateTime;
import java.util.List;

public class OfferingDto {
    private String id;
    private String name;
    private String description;
    private String identifier;
    private OfferingType type;
    private OfferingStatus status;
    private List<OfferingCharacteristicDto> characteristics;
    private BillingConfigDto billingConfig;
    private List<OfferingPriceDto> pricing;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
