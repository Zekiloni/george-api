package com.zekiloni.george.commerce.domain.inventory.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * Per-{@link ServiceAccess} usage counters, kept in its own row so the
 * high-write quota tracking (every campaign batch) doesn't churn the
 * lifecycle-bearing {@link ServiceAccess} entity.
 *
 * <p>Generic across service types: SMTP messages, LEAD pack downloads,
 * GSM SMS — anything that has a "consumed" counter.
 *
 * <p>Limits of {@code 0} mean "unlimited". {@code dailyResetDate} is rolled
 * to the current UTC date by the quota service when needed.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServiceUsage {
    private String id;
    private String serviceAccessId;
    private String tenantId;

    private long totalUsed;
    private long totalLimit;     // 0 = unlimited

    private long dailyUsed;
    private long dailyLimit;     // 0 = unlimited
    private LocalDate dailyResetDate;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
