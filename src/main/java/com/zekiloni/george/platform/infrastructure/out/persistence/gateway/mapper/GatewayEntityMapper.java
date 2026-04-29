package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.mapper;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gatway.smtp.SmtpGateway;
import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity.GatewayEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity.GsmGatewayEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.gateway.entity.SmtpGatewayEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

@Mapper
public interface GatewayEntityMapper {

    @SubclassMappings({
            @SubclassMapping(source = SmtpGatewayEntity.class, target = SmtpGateway.class),
            @SubclassMapping(source = GsmGatewayEntity.class, target = GsmGateway.class)
    })
    Gateway toDomain(GatewayEntity entity);

    SmtpGateway toDomain(SmtpGatewayEntity entity);

    GsmGateway toDomain(GsmGatewayEntity entity);

    @SubclassMappings({
            @SubclassMapping(source = SmtpGateway.class, target = SmtpGatewayEntity.class),
            @SubclassMapping(source = GsmGateway.class, target = GsmGatewayEntity.class)
    })
    GatewayEntity toEntity(Gateway gateway);

    SmtpGatewayEntity toEntity(SmtpGateway gateway);

    GsmGatewayEntity toEntity(GsmGateway gateway);

    @ObjectFactory
    default Gateway createDomain(GatewayEntity entity) {
        if (entity instanceof SmtpGatewayEntity) {
            return new SmtpGateway();
        } else if (entity instanceof GsmGatewayEntity) {
            return new GsmGateway();
        }
        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    @ObjectFactory
    default GatewayEntity createEntity(Gateway gateway) {
        if (gateway instanceof SmtpGateway) {
            return new SmtpGatewayEntity();
        } else if (gateway instanceof GsmGateway) {
            return new GsmGatewayEntity();
        }
        throw new IllegalArgumentException("Unknown domain type: " + gateway.getClass());
    }
}