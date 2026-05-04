package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity;

import com.zekiloni.george.common.infrastructure.out.persistence.entity.TenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service_usage", indexes = {
        @Index(name = "idx_service_usage_service_access_id", columnList = "service_access_id", unique = true)
})
public class ServiceUsageEntity extends TenantEntity {

    @Column(name = "service_access_id", nullable = false, unique = true)
    private String serviceAccessId;

    @Column(name = "total_used", nullable = false)
    private long totalUsed;

    @Column(name = "total_limit", nullable = false)
    private long totalLimit;

    @Column(name = "daily_used", nullable = false)
    private long dailyUsed;

    @Column(name = "daily_limit", nullable = false)
    private long dailyLimit;

    @Column(name = "daily_reset_date")
    private LocalDate dailyResetDate;
}
