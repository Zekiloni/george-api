package com.zekiloni.george.provisioning.infrastructure.in.web.catalog.dto.specification.characteristic;

import com.zekiloni.george.common.infrastructure.in.web.dto.TimePeriodDto;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CharacteristicValueSpecificationCreateDto {
    private Boolean isDefault;
    private String rangeInterval;
    private String regex;
    private String unitOfMeasure;
    private String valueFrom;
    private String valueTo;
    private String valueType;
    private TimePeriodDto validFor;
    private Object value;
}