package com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.mapper;

import com.zekiloni.george.commerce.domain.catalog.model.DiscountTier;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.DiscountTierEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.OfferingEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.specification.characteristic.CharacteristicSpecificationEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.specification.characteristic.CharacteristicValueSpecificationEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingEntityMapper {
    Offering toDomain(OfferingEntity entity);
    CharacteristicSpecification toDomain(CharacteristicSpecificationEntity entity);
    CharacteristicValueSpecification toDomain(CharacteristicValueSpecificationEntity entity);
    DiscountTier toDomain(DiscountTierEntity entity);

    OfferingEntity toEntity(Offering offering);
    CharacteristicSpecificationEntity toEntity(CharacteristicSpecification characteristic);
    CharacteristicValueSpecificationEntity toEntity(CharacteristicValueSpecification valueSpecification);
    DiscountTierEntity toEntity(DiscountTier tier);
}
