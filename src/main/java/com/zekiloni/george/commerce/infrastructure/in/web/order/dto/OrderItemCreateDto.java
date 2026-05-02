package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.CharacteristicCreateDto;

import java.util.List;


public record OrderItemCreateDto(
        String offeringId,
        Integer units,
        List<CharacteristicCreateDto> characteristic
) {
}
