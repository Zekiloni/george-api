package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record GatewayCreateDto(
        @NotNull GatewayType type,
        @NotBlank String name,
        String description,
        @Positive int priority,
        SmtpConfig smtpConfig,
        GsmConfig gsmConfig
) {
    public record SmtpConfig(
            @NotBlank String provider,
            @NotBlank String host,
            @Positive int port,
            @NotBlank String username,
            @NotBlank String password,
            String fromDomain,
            boolean useTls
    ) {}

    public record GsmConfig(
            @NotBlank String provider,
            @NotBlank String ipAddress,
            @Positive int port,
            @NotBlank String username,
            @NotBlank String password,
            @Positive int totalPort
    ) {}
}
