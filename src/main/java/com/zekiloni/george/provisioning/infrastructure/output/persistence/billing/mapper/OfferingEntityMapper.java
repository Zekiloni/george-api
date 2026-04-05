package com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.mapper;

import com.zekiloni.george.provisioning.domain.billing.model.Offering;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.billing.entity.OfferingEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingEntityMapper {
    OfferingEntity toEntity(Offering offering);
    Offering toDomain(OfferingEntity offeringEntity);

    default Object mapObjectToObject(Object obj) {
        return obj;
    }
}

