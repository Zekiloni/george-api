package com.zekiloni.george.infrastructure.output.persistence.billing.mapper;


import com.zekiloni.george.domain.billing.model.Plan;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.PlanEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface PlanEntityMapper {
    PlanEntity toEntity(Plan plan);
    Plan toDomain(PlanEntity planEntity);

    @AfterMapping
    default void linkRelations(@MappingTarget PlanEntity entity) {
        if (entity.getFeatures() != null) {
            entity.getFeatures().forEach(f -> f.setPlan(entity));
        }
        if (entity.getPricing() != null) {
            entity.getPricing().forEach(p -> p.setPlan(entity));
        }
    }
}
