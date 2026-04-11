package com.zekiloni.george.common.infrastructure.in.web.dto;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class CharacteristicDto {
    private String id;
    private String name;
    private Object value;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
