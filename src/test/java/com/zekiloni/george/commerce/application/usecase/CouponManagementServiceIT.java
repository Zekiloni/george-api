package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.CouponManagementUseCase;
import com.zekiloni.george.commerce.domain.promotion.model.Coupon;
import com.zekiloni.george.commerce.domain.promotion.model.CouponDuration;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import com.zekiloni.george.commerce.domain.promotion.model.CouponType;
import com.zekiloni.george.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Verifies the full Coupon round-trip through real PostgreSQL via Testcontainers:
 * persist → look up by code (case-insensitive) → reject duplicate codes → archive.
 *
 * Also proves {@link AbstractIntegrationTest} starts the stack correctly — if this
 * passes, every subsequent {@code @SpringBootTest} extending the base inherits a
 * working DB + Keycloak.
 */
class CouponManagementServiceIT extends AbstractIntegrationTest {

    @Autowired
    private CouponManagementUseCase service;

    @Test
    void createAndFindByCode() {
        Coupon created = service.create(Coupon.builder()
                .code("LAUNCH50")
                .name("Launch promo")
                .type(CouponType.PERCENT)
                .duration(CouponDuration.ONCE)
                .percent(new BigDecimal("0.50"))
                .status(CouponStatus.ACTIVE)
                .build());

        assertThat(created.getId()).isNotNull();

        Optional<Coupon> found = service.findByCode("LAUNCH50");
        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("LAUNCH50");
    }

    @Test
    void findByCodeIsCaseInsensitive() {
        service.create(Coupon.builder()
                .code("SAVE20")
                .type(CouponType.PERCENT)
                .duration(CouponDuration.ONCE)
                .percent(new BigDecimal("0.20"))
                .status(CouponStatus.ACTIVE)
                .build());

        assertThat(service.findByCode("save20")).isPresent();
        assertThat(service.findByCode("Save20")).isPresent();
    }

    @Test
    void duplicateCodeRejected() {
        service.create(Coupon.builder()
                .code("UNIQUE")
                .type(CouponType.PERCENT)
                .duration(CouponDuration.ONCE)
                .percent(new BigDecimal("0.10"))
                .status(CouponStatus.ACTIVE)
                .build());

        assertThatThrownBy(() -> service.create(Coupon.builder()
                .code("UNIQUE")
                .type(CouponType.PERCENT)
                .duration(CouponDuration.ONCE)
                .percent(new BigDecimal("0.20"))
                .status(CouponStatus.ACTIVE)
                .build()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void archiveSetsStatus() {
        Coupon coupon = service.create(Coupon.builder()
                .code("TODELETE")
                .type(CouponType.FIXED_AMOUNT)
                .duration(CouponDuration.ONCE)
                .status(CouponStatus.ACTIVE)
                .build());

        service.archive(coupon.getId());

        Coupon reloaded = service.findById(coupon.getId()).orElseThrow();
        assertThat(reloaded.getStatus()).isEqualTo(CouponStatus.ARCHIVED);
    }
}
