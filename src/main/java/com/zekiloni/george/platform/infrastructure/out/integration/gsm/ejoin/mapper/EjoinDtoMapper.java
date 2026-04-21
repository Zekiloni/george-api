package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.mapper;

import com.zekiloni.george.platform.application.port.out.GsmGatewayPort;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EjoinDtoMapper {
    @Mapping(target = "port", expression = "java(String.valueOf(response.port()))")
    @Mapping(target = "active", expression = "java(Integer.valueOf(1).equals(response.active()))")
    @Mapping(target = "inserted", expression = "java(Integer.valueOf(1).equals(response.inserted()))")
    @Mapping(target = "slotActive", expression = "java(Integer.valueOf(1).equals(response.slotActive()))")
    @Mapping(target = "signalStrength", source = "signalStrength")
    @Mapping(target = "operator", source = "operator")
    @Mapping(target = "imsi", source = "imsi")
    @Mapping(target = "iccid", source = "iccid")
    @Mapping(target = "balance", source = "balance")
    GsmGatewayPort.PortStatus toDomain(EjoinPortStatusResponse response);
}
