package com.zekiloni.george.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.domain.billing.model.Discount;
import com.zekiloni.george.infrastructure.input.web.billing.dto.DiscountCreateDto;
import com.zekiloni.george.infrastructure.input.web.billing.dto.DiscountDto;
import org.mapstruct.Mapper;

@Mapper
public interface DiscountDtoMapper {
    Discount toDomain(DiscountCreateDto dto);

    DiscountDto toDto(Discount discount);
}

