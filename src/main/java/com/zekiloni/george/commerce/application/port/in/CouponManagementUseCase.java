package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.promotion.model.Coupon;

import java.util.List;
import java.util.Optional;

public interface CouponManagementUseCase {
    Coupon create(Coupon coupon);

    Optional<Coupon> findById(String id);

    Optional<Coupon> findByCode(String code);

    List<Coupon> listAll();

    Coupon update(Coupon coupon);

    void archive(String id);
}
