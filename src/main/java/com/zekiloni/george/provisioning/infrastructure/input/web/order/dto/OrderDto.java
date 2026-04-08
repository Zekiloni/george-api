package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import com.zekiloni.george.provisioning.domain.order.model.OrderStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record OrderDto(
        String id,
        List<OrderItemDto> item,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        OrderStatus status
) {
}

