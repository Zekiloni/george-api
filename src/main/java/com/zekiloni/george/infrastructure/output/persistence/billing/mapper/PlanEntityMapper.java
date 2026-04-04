package com.zekiloni.george.infrastructure.output.persistence.billing.mapper;


import com.zekiloni.george.domain.billing.model.Plan;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.PlanEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PlanEntityMapper {
    PlanEntity toEntity(Plan plan);
    Plan toDomain(PlanEntity planEntity);
}
