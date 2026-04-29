package com.zekiloni.george.platform.infrastructure.in.web.gateway.mapper;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
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

    default Gateway toDomain(GatewayCreateDto dto) {
        return switch (dto.type()) {
            case SMTP -> {
                SmtpGateway gateway = new SmtpGateway();
                gateway.setType(dto.type());
                gateway.setName(dto.name());
                gateway.setDescription(dto.description());
                gateway.setPriority(dto.priority());
                gateway.setUsername(dto.smtpConfig().username());
                gateway.setHost(dto.smtpConfig().host());
                gateway.setPort(dto.smtpConfig().port());
                gateway.setFromDomain(dto.smtpConfig().fromDomain());
                gateway.setUseTls(dto.smtpConfig().useTls());
                yield gateway;
            }
            case GSM -> {
                GsmGateway gateway = new GsmGateway();
                gateway.setType(dto.type());
                gateway.setName(dto.name());
                gateway.setDescription(dto.description());
                gateway.setPriority(dto.priority());
                gateway.setUsername(dto.gsmConfig().username());
                gateway.setIpAddress(dto.gsmConfig().ipAddress());
                gateway.setPort(dto.gsmConfig().port());
                gateway.setTotalPort(dto.gsmConfig().totalPort());
                yield gateway;
            }
        };
    }

    @ObjectFactory
    default Gateway createGateway(GatewayCreateDto dto) {
        if (dto.type() == null) {
            throw new IllegalArgumentException("Gateway type cannot be null");
        }
        return switch (dto.type()) {
            case SMTP -> new SmtpGateway();
            case GSM -> new GsmGateway();
        };
    }
}