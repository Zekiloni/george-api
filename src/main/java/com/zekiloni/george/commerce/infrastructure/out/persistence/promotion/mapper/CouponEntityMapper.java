package com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.mapper;

import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.entity.CouponEntity;
import org.mapstruct.Mapper;

@Mapper
public interface CouponEntityMapper {
    Coupon toDomain(CouponEntity entity);

    CouponEntity toEntity(Coupon coupon);
}
