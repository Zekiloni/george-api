package com.zekiloni.george.infrastructure.input.web.billing.dto;

public record OfferingCharacteristicCreateDto(
        String key,
        String name,
        String description,
        Object value
) {
}

