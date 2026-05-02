package com.zekiloni.george.commerce.application.port.out.promotion;

import com.zekiloni.george.commerce.domain.promotion.model.Coupon;

import java.util.Optional;

public interface CouponRepositoryPort {
    Optional<Coupon> findByCode(String code);

    Coupon save(Coupon coupon);

    Optional<Coupon> findById(String id);
}
