package com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic;

import com.zekiloni.george.common.infrastructure.in.web.dto.TimePeriodDto;
import com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.PriceImpact;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicSpecificationDto {
    private String id;

    private String name;

    private String description;

    private String valueType;

    private Boolean configurable;

    private Boolean extensible;

    private Boolean isUnique;

    private Integer maxCardinality;

    private Integer minCardinality;

    private String regex;

    private TimePeriodDto validFor;

    private PriceImpact priceImpact;

    private List<CharacteristicValueSpecificationDto> characteristicValueSpecification;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}