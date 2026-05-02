package com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.repository;

import com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, UUID> {
    Optional<CouponEntity> findByCodeIgnoreCase(String code);
}
