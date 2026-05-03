package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;

import java.util.Map;

public record GsmGatewayDto(
        String id,
        GatewayType type,
        String name,
        String description,
        boolean enabled,
        int priority,
        Map<String, String> config,
        GsmProvider provider,
        int activePortCount
) implements GatewayDto {}
