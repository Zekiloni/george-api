package com.zekiloni.george.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.domain.billing.model.Plan;
import com.zekiloni.george.domain.billing.model.PlanFeature;
import com.zekiloni.george.domain.billing.model.PeriodPrice;
import com.zekiloni.george.infrastructure.input.web.billing.dto.*;
import org.mapstruct.Mapper;

@Mapper
public interface PlanDtoMapper {
    Plan toDomain(PlanCreateDto planCreate);

    PlanDto toDto(Plan plan);

    // PlanFeature mapping
    PlanFeature toDomain(PlanFeatureCreateDto dto);

    PlanFeatureDto toDto(PlanFeature planFeature);

    PeriodPrice toDomain(PeriodPriceCreateDto dto);

    PeriodPriceDto toDto(PeriodPrice periodPrice);
}
