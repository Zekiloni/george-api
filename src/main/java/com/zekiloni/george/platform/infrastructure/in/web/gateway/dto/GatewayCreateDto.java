package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.util.Map;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = SmtpGatewayCreateDto.class, name = "SMTP"),
        @JsonSubTypes.Type(value = GsmGatewayCreateDto.class, name = "GSM")
})
public abstract class GatewayCreateDto {
    private GatewayType type;

    @NotBlank
    private String name;

    private String description;

    @PositiveOrZero
    private int priority;

    @NotNull
    private Map<String, String> config;
}
