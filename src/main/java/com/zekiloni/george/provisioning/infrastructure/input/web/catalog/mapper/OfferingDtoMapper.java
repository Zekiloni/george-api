package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.mapper;

import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingPrice;
import com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic.CharacteristicSpecification;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.*;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingDtoMapper {
    Offering toDomain(OfferingCreateDto offeringCreate);

    OfferingDto toDto(Offering offering);

    CharacteristicSpecification toDomain(OfferingCharacteristicCreateDto dto);

    OfferingCharacteristicDto toDto(CharacteristicSpecification offeringCharacteristic);

    OfferingPrice toDomain(OfferingPriceCreateDto dto);

    OfferingPriceDto toDto(OfferingPrice periodPrice);
}

