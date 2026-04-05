package com.zekiloni.george.provisioning.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.provisioning.domain.billing.model.Discount;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.DiscountCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.DiscountDto;
import org.mapstruct.Mapper;

@Mapper
public interface DiscountDtoMapper {
    Discount toDomain(DiscountCreateDto dto);

    DiscountDto toDto(Discount discount);
}

