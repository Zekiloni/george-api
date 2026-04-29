package com.zekiloni.george.platform.infrastructure.in.web.gateway.mapper;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GsmGatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.SmtpGatewayCreateDto;
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

    @SubclassMappings({
            @SubclassMapping(source = SmtpGatewayCreateDto.class, target = SmtpGateway.class),
            @SubclassMapping(source = GsmGatewayCreateDto.class, target = GsmGateway.class)
    })

    Gateway toDomain(GatewayCreateDto dto);

    @ObjectFactory
    default Gateway createGateway(GatewayCreateDto dto) {
        return switch (dto) {
            case SmtpGatewayCreateDto smtp -> new SmtpGateway();
            case GsmGatewayCreateDto gsm -> new GsmGateway();
            default -> throw new IllegalStateException("Unexpected value: " + dto);
        };
    }

}