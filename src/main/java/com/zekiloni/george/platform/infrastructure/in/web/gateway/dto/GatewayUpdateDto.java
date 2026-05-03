package com.zekiloni.george.platform.infrastructure.in.web.gateway.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;

import java.util.Map;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GatewayUpdateDto.SmtpUpdate.class, name = "SMTP"),
        @JsonSubTypes.Type(value = GatewayUpdateDto.GsmUpdate.class, name = "GSM")
})
public sealed interface GatewayUpdateDto {
    String name();
    String description();
    Integer priority();
    Map<String, String> config();

    record SmtpUpdate(
            String name,
            String description,
            Integer priority,
            Map<String, String> config,
            SmtpGatewayProvider provider
    ) implements GatewayUpdateDto {}

    record GsmUpdate(
            String name,
            String description,
            Integer priority,
            Map<String, String> config,
            GsmProvider provider
    ) implements GatewayUpdateDto {}
}
