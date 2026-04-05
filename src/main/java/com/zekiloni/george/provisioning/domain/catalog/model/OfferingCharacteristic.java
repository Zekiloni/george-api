package com.zekiloni.george.provisioning.domain.catalog.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferingCharacteristic {
    private String id;
    private String key;
    private String name;
    private String description;
    private Object value;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

