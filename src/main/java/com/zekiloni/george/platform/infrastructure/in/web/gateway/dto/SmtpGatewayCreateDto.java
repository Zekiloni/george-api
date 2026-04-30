package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class SmtpGatewayCreateDto extends GatewayCreateDto {
    private @NotNull GatewayType type;
    private @NotBlank String name;
    private String description;
    private @Positive int priority;
    private @NotBlank String username;
    private @NotBlank String password;
    private @NotBlank String provider;
    private @NotBlank String host;
    private @Positive int port;
    private String fromDomain;
    private boolean useTls;
    private String adminUrl;
}