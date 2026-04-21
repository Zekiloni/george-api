package com.zekiloni.george.provisioning.infrastructure.in.web.order.dto;


import java.util.List;

public record OrderCreateDto(
        List<OrderItemCreateDto> item
) {
}

