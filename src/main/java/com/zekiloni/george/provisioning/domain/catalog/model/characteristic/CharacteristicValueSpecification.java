package com.zekiloni.george.provisioning.domain.catalog.model.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;

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
}