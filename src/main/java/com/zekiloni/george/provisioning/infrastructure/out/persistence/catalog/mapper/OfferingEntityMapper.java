package com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.mapper;

import com.zekiloni.george.provisioning.domain.catalog.model.*;
import com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.entity.*;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.entity.specification.characteristic.CharacteristicSpecificationEntity;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.catalog.entity.specification.characteristic.CharacteristicValueSpecificationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingEntityMapper {
    Offering toDomain(OfferingEntity entity);
    CharacteristicSpecification toDomain(CharacteristicSpecificationEntity entity);
    CharacteristicValueSpecification toDomain(CharacteristicValueSpecificationEntity entity);
    OfferingPrice toDomain(OfferingPriceEntity entity);
    BillingConfig toDomain(BillingConfigEntity entity);

    OfferingEntity toEntity(Offering offering);
    CharacteristicSpecificationEntity toEntity(CharacteristicSpecification characteristic);
    CharacteristicValueSpecificationEntity toEntity(CharacteristicValueSpecification valueSpecification);
    OfferingPriceEntity toEntity(OfferingPrice price);
    BillingConfigEntity toEntity(BillingConfig billingConfig);
}

