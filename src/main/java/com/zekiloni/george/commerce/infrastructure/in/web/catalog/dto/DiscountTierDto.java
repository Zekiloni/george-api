package com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiscountTierDto {
    private String id;
    private Integer fromUnits;
    private BigDecimal discount;
}
