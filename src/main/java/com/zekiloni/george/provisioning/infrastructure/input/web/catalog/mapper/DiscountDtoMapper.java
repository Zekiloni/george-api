package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.mapper;

import com.zekiloni.george.provisioning.domain.catalog.model.Discount;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.DiscountCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.DiscountDto;
import org.mapstruct.Mapper;

@Mapper
public interface DiscountDtoMapper {
    Discount toDomain(DiscountCreateDto dto);

    DiscountDto toDto(Discount discount);
}

