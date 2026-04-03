package com.zekiloni.george.domain.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanFeature {
    private String key;
    private String name;
    private String description;
    private Object value;
}
