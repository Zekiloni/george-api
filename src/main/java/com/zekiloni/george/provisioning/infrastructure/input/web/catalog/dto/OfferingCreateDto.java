package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

import com.zekiloni.george.provisioning.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingType;

import java.time.OffsetDateTime;
import java.util.List;

public record OfferingCreateDto(
        String name,
        String description,
        String identifier,
        OfferingType type,
        OfferingStatus status,
        List<OfferingCharacteristicCreateDto> characteristics,
        List<OfferingPriceCreateDto> pricing,
        DiscountDto discount,
        OffsetDateTime validFrom,
        OffsetDateTime validTo
) {
}

