package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingDto;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderItemDto(
        String id,
        OfferingDto offering,
        Integer units,
        List<Characteristic> characteristic,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
