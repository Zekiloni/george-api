package com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto;

import com.zekiloni.george.commerce.domain.catalog.model.DurationUnit;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingConfigCreateDto {
    private OfferingType type;
    private boolean quantityAllowed;     
    private Integer maxQuantity;         
    private boolean durationAllowed;
    private DurationUnit durationUnit;
}