package com.zekiloni.george.infrastructure.input.web.billing.dto;

import com.zekiloni.george.domain.billing.model.OfferingType;
import java.time.OffsetDateTime;
import java.util.List;

public record OfferingCreateDto(
        String name,
        String description,
        String identifier,
        OfferingType type,
        List<OfferingCharacteristicCreateDto> characteristics,
        List<OfferingPriceCreateDto> pricing,
        DiscountDto discount,
        OffsetDateTime validFrom,
        OffsetDateTime validTo
) {
}

