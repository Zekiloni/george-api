package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.mapper;

import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPort;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmPortStatus;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortState;
import com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto.EjoinPortStatusResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface EjoinDtoMapper {

    @Mapping(target = "id",             source = "port")
    @Mapping(target = "status",         source = "st")
    @Mapping(target = "phoneNumber",    source = "serialNumber")
    @Mapping(target = "operator",       source = "operator")
    @Mapping(target = "signalStrength", source = "signalStrength")
    @Mapping(target = "balance",        source = "balance")
    GsmPort toDomain(EjoinPortStatusResponse response);

    default GsmPortStatus map(EjoinPortState state) {
        if (state == null) return GsmPortStatus.UNKNOWN;
        return switch (state) {
            case NO_SIM_CARD -> GsmPortStatus.NO_SIM;
            case IDLE -> GsmPortStatus.IDLE;
            case REGISTERING, CARD_DETECTED, ACCESS_MOBILE_NETWORK -> GsmPortStatus.REGISTERING;
            case REGISTERED -> GsmPortStatus.REGISTERED;
            case CALL_CONNECTED, INTER_CALLING, INTER_CALLING_HOLDING -> GsmPortStatus.IN_CALL;
            case NO_BALANCE_OR_ALARM, REGISTER_FAILED, LOCKED_BY_DEVICE,
                 LOCKED_BY_OPERATOR, SIM_ERROR, USER_LOCKED, MODULE_TIMEOUT -> GsmPortStatus.ERROR;
        };
    }

    default int signalDefault(Integer raw) {
        return raw == null ? -1 : raw;
    }
}
