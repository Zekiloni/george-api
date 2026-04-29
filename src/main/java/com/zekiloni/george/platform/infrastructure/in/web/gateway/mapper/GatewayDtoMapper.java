package com.zekiloni.george.platform.infrastructure.in.web.gateway.mapper;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayDto;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

@Mapper
public interface GatewayDtoMapper {

    @SubclassMappings({
            @SubclassMapping(source = SmtpGateway.class, target = GatewayDto.class),
            @SubclassMapping(source = GsmGateway.class, target = GatewayDto.class)
    })
    GatewayDto toDto(Gateway gateway);

    @ObjectFactory
    default Gateway toDomain(GatewayCreateDto dto) {
        if (dto.type() == null) {
            throw new IllegalArgumentException("Gateway type cannot be null");
        }
        return switch (dto.type()) {
            case SMTP -> new SmtpGateway();
            case GSM -> new GsmGateway();
        };
    }
}