package com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto;

import com.zekiloni.george.provisioning.domain.billing.model.OfferingStatus;
import com.zekiloni.george.provisioning.domain.billing.model.OfferingType;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;

import java.time.OffsetDateTime;
import java.util.Set;

public record OfferingDto(
        String id,
        String name,
        String description,
        String identifier,
        OfferingType type,
        OfferingStatus status,
        Set<OfferingCharacteristicDto> characteristics,
        Set<OfferingPriceDto> pricing,
        MoneyDto monthlyPrice,
        DiscountDto discount,
        OffsetDateTime validFrom,
        OffsetDateTime validTo,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

