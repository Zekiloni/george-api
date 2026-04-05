package com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto;

public record OfferingCharacteristicCreateDto(
        String key,
        String name,
        String description,
        Object value
) {
}

