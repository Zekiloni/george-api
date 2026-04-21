package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.CharacteristicCreateDto;
import com.zekiloni.george.commerce.domain.catalog.model.DurationUnit;

import java.util.List;


public record OrderItemCreateDto(
        String offeringId,
        Integer quantity,
        Integer duration,
        DurationUnit durationUnit,
        List<CharacteristicCreateDto> characteristic
) {
}

