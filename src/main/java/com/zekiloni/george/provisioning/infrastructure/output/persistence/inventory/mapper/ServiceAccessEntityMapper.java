package com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.mapper;

import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity.ServiceAccessEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ServiceAccessEntityMapper {
    ServiceAccess toDomain(ServiceAccessEntity entity);

    ServiceAccessEntity toEntity(ServiceAccess domain);
}
