package com.zekiloni.george.commerce.infrastructure.in.web.promotion;

import com.zekiloni.george.commerce.application.port.in.CouponManagementUseCase;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.infrastructure.in.web.promotion.dto.CouponCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.promotion.dto.CouponDto;
import com.zekiloni.george.commerce.infrastructure.in.web.promotion.mapper.CouponDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.base.path:/api/v1}/coupons")
@RequiredArgsConstructor
public class CouponApiController {

    private final CouponManagementUseCase service;
    private final CouponDtoMapper mapper;

    @PreAuthorize("hasRole('admin')")
    @GetMapping
    public List<CouponDto> list() {
        return service.listAll().stream().map(mapper::toDto).toList();
    }

    /** Customer-facing: find by code (used at checkout to preview the discount). */
    @GetMapping("/code/{code}")
    public ResponseEntity<CouponDto> findByCode(@PathVariable String code) {
        return service.findByCode(code)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    @GetMapping("/{id}")
    public ResponseEntity<CouponDto> getById(@PathVariable String id) {
        return service.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PreAuthorize("hasRole('admin')")
    @PostMapping
    public ResponseEntity<CouponDto> create(@RequestBody CouponCreateDto dto) {
        Coupon created = service.create(mapper.toDomain(dto));
        return ResponseEntity.ok(mapper.toDto(created));
    }

    @PreAuthorize("hasRole('admin')")
    @PutMapping("/{id}")
    public ResponseEntity<CouponDto> update(@PathVariable String id, @RequestBody CouponDto dto) {
        dto.setId(id);
        // Reuse the toDomain mapping for update by going through CouponDto-typed flow
        Coupon coupon = service.findById(id).orElseThrow();
        coupon.setName(dto.getName());
        coupon.setDescription(dto.getDescription());
        coupon.setType(dto.getType());
        coupon.setDuration(dto.getDuration());
        coupon.setPercent(dto.getPercent());
        coupon.setFixedAmount(dto.getFixedAmount());
        coupon.setDurationInPeriods(dto.getDurationInPeriods());
        coupon.setMinOrderAmount(dto.getMinOrderAmount());
        coupon.setAppliesToOfferingIds(dto.getAppliesToOfferingIds());
        coupon.setValidFrom(dto.getValidFrom());
        coupon.setValidTo(dto.getValidTo());
        coupon.setMaxRedemptions(dto.getMaxRedemptions());
        coupon.setStatus(dto.getStatus());
        return ResponseEntity.ok(mapper.toDto(service.update(coupon)));
    }

    @PreAuthorize("hasRole('admin')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> archive(@PathVariable String id) {
        service.archive(id);
        return ResponseEntity.noContent().build();
    }
}
