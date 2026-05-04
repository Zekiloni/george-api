package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.mapper;

import com.zekiloni.george.commerce.domain.inventory.model.ServiceUsage;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceUsageEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceUsageEntityMapper {
    ServiceUsage toDomain(ServiceUsageEntity entity);
    ServiceUsageEntity toEntity(ServiceUsage domain);
}
