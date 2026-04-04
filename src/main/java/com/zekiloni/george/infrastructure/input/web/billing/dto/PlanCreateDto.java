package com.zekiloni.george.infrastructure.input.web.billing.dto;

import java.util.List;

public record PlanCreateDto(String name, String description, String identifier, List<PlanFeatureCreateDto> features,
                            List<PeriodPriceCreateDto> pricing, boolean isActive, boolean isPublic) {
}
