package com.zekiloni.george.provisioning.domain.catalog.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BillingConfig {
    private OfferingType type;
    private boolean quantityAllowed;     
    private Integer maxQuantity;         
    private boolean durationAllowed;
    private DurationUnit durationUnit;
}