package com.zekiloni.george.platform.domain.model.gateway;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class Gateway {
    private String id;
    private GatewayType type;
    private String name;
    private String description;
    private boolean enabled;
    private int priority;

    // Free-form per-provider config — keys defined in GatewayConfigKeys.
    // Each provider enum's publicKeys() / secretKeys() declare what it expects.
    private Map<String, String> config = new HashMap<>();

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
