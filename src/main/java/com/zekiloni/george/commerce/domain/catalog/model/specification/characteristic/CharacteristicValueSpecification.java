package com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacteristicValueSpecification {
    private String id;

    private Boolean isDefault;
    
    private String rangeInterval;

    private String regex;
    
    private String unitOfMeasure;
    
    private String valueFrom;
    
    private String valueTo;
    
    private String valueType;
    
    private TimePeriod validFor;
    
    private Object value;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}