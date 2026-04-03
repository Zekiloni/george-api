package com.zekiloni.george.domain.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

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
    private Pricing pricing;
    private boolean isActive;
    private boolean isPublic;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
