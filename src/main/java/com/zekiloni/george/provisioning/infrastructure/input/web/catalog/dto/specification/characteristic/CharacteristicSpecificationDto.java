package com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.specification.characteristic;

import com.zekiloni.george.common.infrastructure.in.web.dto.TimePeriodDto;
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

    private List<CharacteristicValueSpecificationDto> characteristicValueSpecification;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}