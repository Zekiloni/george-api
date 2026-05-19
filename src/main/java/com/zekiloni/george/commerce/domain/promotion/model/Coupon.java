package com.zekiloni.george.commerce.domain.promotion.model;

import com.zekiloni.george.common.domain.model.Money;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {
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
    @Builder.Default
    private int redeemedCount = 0;

    private CouponStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
