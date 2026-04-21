package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.mapper;

import com.zekiloni.george.platform.application.port.out.GsmGatewayPort;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortStatusResponse;
import org.mapstruct.Mapper;

@Mapper
public interface EjoinDtoMapper {
    GsmGatewayPort.PortStatus toDomain(EjoinPortStatusResponse response);

    default boolean activeToBoolean(Integer active) {
        return Integer.valueOf(1).equals(active);
    }
}
