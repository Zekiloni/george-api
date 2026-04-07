package com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;

import java.time.OffsetDateTime;

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