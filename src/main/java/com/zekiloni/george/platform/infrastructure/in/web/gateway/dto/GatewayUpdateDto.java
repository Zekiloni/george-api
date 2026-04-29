package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

public record GatewayUpdateDto(
        String name,
        String description,
        Integer priority,
        SmtpConfig smtpConfig,
        GsmConfig gsmConfig
) {
    public record SmtpConfig(
            String provider,
            String host,
            Integer port,
            String username,
            String password,
            String fromDomain,
            Boolean useTls
    ) {}

    public record GsmConfig(
            String provider,
            String ipAddress,
            Integer port,
            String username,
            String password,
            Integer totalPort
    ) {}
}
