package com.zekiloni.george.infrastructure.output.persistence.billing.mapper;

import com.zekiloni.george.domain.billing.model.Offering;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.OfferingEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingEntityMapper {
    OfferingEntity toEntity(Offering offering);
    Offering toDomain(OfferingEntity offeringEntity);

    default Object mapObjectToObject(Object obj) {
        return obj;
    }
}

