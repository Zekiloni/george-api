package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.mapper;

import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingCharacteristic;
import com.zekiloni.george.provisioning.domain.catalog.model.OfferingPrice;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.*;
import org.mapstruct.Mapper;

@Mapper
public interface OfferingDtoMapper {
    Offering toDomain(OfferingCreateDto offeringCreate);

    OfferingDto toDto(Offering offering);

    OfferingCharacteristic toDomain(OfferingCharacteristicCreateDto dto);

    OfferingCharacteristicDto toDto(OfferingCharacteristic offeringCharacteristic);

    OfferingPrice toDomain(OfferingPriceCreateDto dto);

    OfferingPriceDto toDto(OfferingPrice periodPrice);
}

