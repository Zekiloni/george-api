package com.zekiloni.george.commerce.infrastructure.out.persistence.promotion.entity;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.common.infrastructure.out.persistence.entity.BaseEntity;
import com.zekiloni.george.commerce.domain.promotion.model.CouponDuration;
import com.zekiloni.george.commerce.domain.promotion.model.CouponStatus;
import com.zekiloni.george.commerce.domain.promotion.model.CouponType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "coupons")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class CouponEntity extends BaseEntity {

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CouponType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "duration", nullable = false)
    private CouponDuration duration;

    @Column(name = "percent", precision = 5, scale = 4)
    private BigDecimal percent;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "fixed_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "fixed_currency"))
    })
    @Embedded
    private Money fixedAmount;

    @Column(name = "duration_in_periods")
    private Integer durationInPeriods;

    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "min_order_amount")),
            @AttributeOverride(name = "currency", column = @Column(name = "min_order_currency"))
    })
    @Embedded
    private Money minOrderAmount;

    @Column(name = "applies_to_offering_ids", columnDefinition = "jsonb")
    @JdbcTypeCode(SqlTypes.JSON)
    private List<String> appliesToOfferingIds;

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;

    @Column(name = "max_redemptions")
    private Integer maxRedemptions;

    @Column(name = "redeemed_count", nullable = false)
    private int redeemedCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status;
}
