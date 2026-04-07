package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.mapper;

import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingPrice;
import com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic.CharacteristicValueSpecification;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.*;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.specification.characteristic.CharacteristicSpecificationCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.specification.characteristic.CharacteristicSpecificationDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.specification.characteristic.CharacteristicValueSpecificationCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.specification.characteristic.CharacteristicValueSpecificationDto;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingDtoMapper {
    Offering toDomain(OfferingCreateDto offeringCreate);

    CharacteristicSpecification toDomain(CharacteristicSpecificationCreateDto dto);

    CharacteristicValueSpecificationDto toDomain(CharacteristicValueSpecificationCreateDto dto);

    OfferingDto toDto(Offering offering);

    CharacteristicSpecificationDto toDto(CharacteristicSpecification offeringCharacteristic);

    CharacteristicValueSpecificationDto toDto(CharacteristicValueSpecification offeringCharacteristic);

    OfferingPrice toDomain(OfferingPriceCreateDto dto);

    OfferingPriceDto toDto(OfferingPrice periodPrice);
}

