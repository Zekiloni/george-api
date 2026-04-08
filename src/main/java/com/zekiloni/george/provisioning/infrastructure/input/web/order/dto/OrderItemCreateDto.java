package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.CharacteristicCreateDto;
import com.zekiloni.george.provisioning.domain.catalog.model.DurationUnit;

import java.util.List;


public record OrderItemCreateDto(
        String offeringId,
        Integer quantity,
        Integer duration,
        DurationUnit durationUnit,
        List<CharacteristicCreateDto> characteristic
) {
}

