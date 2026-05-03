package com.zekiloni.george.platform.infrastructure.in.web.gateway.mapper;

import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GsmGatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GsmGatewayDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.SmtpGatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.SmtpGatewayDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

@Mapper
public interface GatewayDtoMapper {

    default GatewayDto toDto(Gateway gateway) {
        return switch (gateway) {
            case SmtpGateway smtp -> toDto(smtp);
            case GsmGateway gsm -> toDto(gsm);
            default -> throw new IllegalArgumentException("Unsupported gateway type: " + gateway.getClass());
        };
    }

    @Mapping(target = "config", expression = "java(redact(gateway.getConfig(), gateway.getProvider() == null ? java.util.Set.of() : gateway.getProvider().secretKeys()))")
    SmtpGatewayDto toDto(SmtpGateway gateway);

    @Mapping(target = "config", expression = "java(redact(gateway.getConfig(), gateway.getProvider() == null ? java.util.Set.of() : gateway.getProvider().secretKeys()))")
    GsmGatewayDto toDto(GsmGateway gateway);

    default Gateway toDomain(GatewayCreateDto dto) {
        return switch (dto) {
            case SmtpGatewayCreateDto smtp -> toDomain(smtp);
            case GsmGatewayCreateDto gsm -> toDomain(gsm);
            default -> throw new IllegalArgumentException("Unsupported create DTO: " + dto.getClass());
        };
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "SMTP")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    SmtpGateway toDomain(SmtpGatewayCreateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "type", constant = "GSM")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "slot", ignore = true)
    GsmGateway toDomain(GsmGatewayCreateDto dto);

    default Map<String, String> redact(Map<String, String> config, Set<String> secretKeys) {
        if (config == null) return Map.of();
        Map<String, String> out = new LinkedHashMap<>(config.size());
        for (Map.Entry<String, String> e : config.entrySet()) {
            String value = secretKeys.contains(e.getKey()) && e.getValue() != null && !e.getValue().isEmpty()
                    ? GatewayConfigKeys.REDACTED
                    : e.getValue();
            out.put(e.getKey(), value);
        }
        return out;
    }
}
