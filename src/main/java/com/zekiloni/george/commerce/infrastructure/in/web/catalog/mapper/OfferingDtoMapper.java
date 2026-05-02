package com.zekiloni.george.commerce.infrastructure.in.web.catalog.mapper;

import com.zekiloni.george.commerce.domain.catalog.model.DiscountTier;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.DiscountTierCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.DiscountTierDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic.CharacteristicSpecificationCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic.CharacteristicSpecificationDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic.CharacteristicValueSpecificationCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic.CharacteristicValueSpecificationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OfferingDtoMapper {

    @Mapping(target = "id", ignore = true)
    Offering toDomain(OfferingCreateDto offeringCreate);

    CharacteristicSpecification toDomain(CharacteristicSpecificationCreateDto dto);

    CharacteristicValueSpecification toDomain(CharacteristicValueSpecificationCreateDto dto);

    DiscountTier toDomain(DiscountTierCreateDto dto);

    OfferingDto toDto(Offering offering);

    CharacteristicSpecificationDto toDto(CharacteristicSpecification offeringCharacteristic);

    CharacteristicValueSpecificationDto toDto(CharacteristicValueSpecification offeringCharacteristic);

    DiscountTierDto toDto(DiscountTier tier);
}
