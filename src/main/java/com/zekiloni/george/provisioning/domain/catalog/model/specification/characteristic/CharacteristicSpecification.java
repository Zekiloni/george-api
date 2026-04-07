package com.zekiloni.george.provisioning.domain.catalog.model.specification.characteristic;

import com.zekiloni.george.common.domain.model.TimePeriod;

import java.time.OffsetDateTime;
import java.util.List;

public class CharacteristicSpecification {
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
    
    private TimePeriod validFor;
    
    private List<CharacteristicValueSpecification> characteristicValueSpecification;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}