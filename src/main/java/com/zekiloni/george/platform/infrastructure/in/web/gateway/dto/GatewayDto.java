package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SmtpGatewayDto.class, name = "SMTP"),
        @JsonSubTypes.Type(value = GsmGatewayDto.class, name = "GSM")
})
public sealed interface GatewayDto permits SmtpGatewayDto, GsmGatewayDto {
    String id();
    GatewayType type();
    String name();
    String description();
    boolean enabled();
    int priority();

    // Provider config; secret values (apiKey, password, ...) are returned as "***".
    Map<String, String> config();
}
