package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.platform.domain.model.campaign.conversion.Conversion;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.ConversionEntity;
import org.mapstruct.Mapper;

@Mapper
public interface ConversionEntityMapper {

    ConversionEntity toEntity(Conversion conversion);

    Conversion toDomain(ConversionEntity entity);
}
