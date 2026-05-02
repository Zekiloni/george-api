package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.CouponManagementUseCase;
import com.zekiloni.george.commerce.application.port.out.promotion.CouponRepositoryPort;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponManagementService implements CouponManagementUseCase {

    private final CouponRepositoryPort repository;

    @Override
    @Transactional
    public Coupon create(Coupon coupon) {
        if (coupon.getCode() == null || coupon.getCode().isBlank()) {
            throw new IllegalArgumentException("Coupon code is required");
        }
        repository.findByCode(coupon.getCode()).ifPresent(existing -> {
            throw new IllegalArgumentException("Coupon code '" + existing.getCode() + "' already exists");
        });
        if (coupon.getStatus() == null) coupon.setStatus(CouponStatus.ACTIVE);
        return repository.save(coupon);
    }

    @Override
    public Optional<Coupon> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Coupon> findByCode(String code) {
        return repository.findByCode(code);
    }

    @Override
    public List<Coupon> listAll() {
        return repository.findAll();
    }

    @Override
    @Transactional
    public Coupon update(Coupon coupon) {
        if (coupon.getId() == null) throw new IllegalArgumentException("Coupon id is required for update");
        Coupon existing = repository.findById(coupon.getId())
                .orElseThrow(() -> new NoSuchElementException("Coupon " + coupon.getId() + " not found"));
        // Code is immutable once issued (preserves audit trail of redemptions)
        coupon.setCode(existing.getCode());
        coupon.setRedeemedCount(existing.getRedeemedCount());
        return repository.save(coupon);
    }

    @Override
    @Transactional
    public void archive(String id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Coupon " + id + " not found"));
        coupon.setStatus(CouponStatus.ARCHIVED);
        repository.save(coupon);
    }
}
