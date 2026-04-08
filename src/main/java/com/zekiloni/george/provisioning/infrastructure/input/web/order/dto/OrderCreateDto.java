package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;


import java.util.List;

public record OrderCreateDto(
        List<OrderItemCreateDto> item
) {
}

