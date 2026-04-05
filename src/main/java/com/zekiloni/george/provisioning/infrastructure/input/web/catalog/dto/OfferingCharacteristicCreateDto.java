package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto;

public record OfferingCharacteristicCreateDto(
        String key,
        String name,
        String description,
        Object value
) {
}

