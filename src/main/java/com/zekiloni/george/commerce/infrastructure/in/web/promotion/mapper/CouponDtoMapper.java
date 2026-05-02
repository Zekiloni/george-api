package com.zekiloni.george.commerce.infrastructure.in.web.promotion.mapper;

import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.infrastructure.in.web.promotion.dto.CouponCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.promotion.dto.CouponDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CouponDtoMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "redeemedCount", constant = "0")
    Coupon toDomain(CouponCreateDto dto);

    CouponDto toDto(Coupon coupon);
}
