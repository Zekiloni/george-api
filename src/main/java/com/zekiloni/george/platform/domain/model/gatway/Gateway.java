package com.zekiloni.george.platform.domain.model.gatway;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class Gateway {
    private String id;
    private GatewayType type;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
