package com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.adapter;

import com.zekiloni.george.commerce.application.port.out.promotion.CouponRepositoryPort;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.entity.CouponEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.mapper.CouponEntityMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.repository.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CouponRepositoryPortAdapter implements CouponRepositoryPort {

    private final CouponJpaRepository repository;
    private final CouponEntityMapper mapper;

    @Override
    public Optional<Coupon> findByCode(String code) {
        if (code == null || code.isBlank()) return Optional.empty();
        return repository.findByCodeIgnoreCase(code).map(mapper::toDomain);
    }

    @Override
    public Coupon save(Coupon coupon) {
        CouponEntity entity = mapper.toEntity(coupon);
        return mapper.toDomain(repository.save(entity));
    }

    @Override
    public Optional<Coupon> findById(String id) {
        if (id == null) return Optional.empty();
        try {
            return repository.findById(UUID.fromString(id)).map(mapper::toDomain);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
