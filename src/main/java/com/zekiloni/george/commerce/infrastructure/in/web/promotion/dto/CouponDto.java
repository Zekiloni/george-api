package com.zekiloni.george.commerce.infrastructure.in.web.promotion.dto;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.domain.promotion.model.CouponDuration;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import com.zekiloni.george.commerce.domain.promotion.model.CouponType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponDto {
    private String id;
    private String code;
    private String name;
    private String description;
    private CouponType type;
    private CouponDuration duration;
    private BigDecimal percent;
    private Money fixedAmount;
    private Integer durationInPeriods;
    private Money minOrderAmount;
    private List<String> appliesToOfferingIds;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private Integer maxRedemptions;
    private int redeemedCount;
    private CouponStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
