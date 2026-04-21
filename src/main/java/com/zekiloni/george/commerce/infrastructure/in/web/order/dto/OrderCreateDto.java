package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;


import java.util.List;

public record OrderCreateDto(
        List<OrderItemCreateDto> item
) {
}

