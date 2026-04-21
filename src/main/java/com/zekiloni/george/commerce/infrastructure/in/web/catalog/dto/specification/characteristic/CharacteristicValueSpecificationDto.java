package com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.specification.characteristic;

import com.zekiloni.george.common.infrastructure.in.web.dto.TimePeriodDto;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicValueSpecificationDto {

    private String id;

    private Boolean isDefault;

    private String rangeInterval;

    private String regex;

    private String unitOfMeasure;

    private String valueFrom;

    private String valueTo;

    private String valueType;

    private TimePeriodDto validFor;

    private Object value;

    private OffsetDateTime createdAt;

    private OffsetDateTime updatedAt;
}