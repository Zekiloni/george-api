package com.zekiloni.george.commerce.domain.catalog.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingConfig {
    private String id;
    private OfferingType type;
    private boolean quantityAllowed;     
    private Integer maxQuantity;         
    private boolean durationAllowed;
    private DurationUnit durationUnit;
}