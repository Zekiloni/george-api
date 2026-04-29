package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gateway.GatewayType;

public record GatewayDto(
        String id,
        GatewayType type,
        String name,
        String description,
        boolean enabled,
        int priority,
        String username,
        String provider,
        String host,
        String ipAddress,
        int port,
        String fromDomain,
        boolean useTls,
        int totalPort
) {}
