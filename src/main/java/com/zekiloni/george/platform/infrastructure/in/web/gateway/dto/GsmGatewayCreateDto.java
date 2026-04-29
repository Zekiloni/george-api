package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class GsmGatewayCreateDto extends GatewayCreateDto {
    @NotNull GatewayType type;
    @NotBlank String name;
    String description;
    @Positive int priority;
    @NotBlank String username;
    @NotBlank String password;
    @NotBlank String provider;
    @NotBlank String ipAddress;
    @Positive int port;
    @Positive int totalPort;
}