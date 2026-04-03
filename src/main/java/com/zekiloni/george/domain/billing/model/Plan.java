package com.zekiloni.george.domain.billing.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DTO for {@link com.zekiloni.george.infrastructure.persistence.billing.entity.PlanEntity}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Plan {
    private String id;
    private String name;
    private String description;
    private String identifier;
    private PlanFeature features;
    private Set<PeriodPrice> pricing = new HashSet<>();
    private boolean isActive;
    private boolean isPublic;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
