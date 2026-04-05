package com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.mapper;

import com.zekiloni.george.provisioning.domain.catalog.model.*;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity.*;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingEntityMapper {
    Offering toDomain(OfferingEntity entity);
    OfferingCharacteristic toDomain(OfferingCharacteristicEntity entity);
    OfferingPrice toDomain(OfferingPriceEntity entity);
    BillingConfig toDomain(BillingConfigEntity entity);

    OfferingEntity toEntity(Offering offering);
    OfferingCharacteristicEntity toEntity(OfferingCharacteristic characteristic);
    OfferingPriceEntity toEntity(OfferingPrice price);
    BillingConfigEntity toEntity(BillingConfig billingConfig);
}

