package com.zekiloni.george.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ref {
    private String id;
    private String name;
    private String href;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
