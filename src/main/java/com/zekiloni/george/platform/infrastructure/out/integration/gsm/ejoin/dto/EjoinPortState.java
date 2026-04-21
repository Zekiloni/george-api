package com.zekiloni.george.platform.infrastructure.out.integration.gsm.ejoin.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;

@Getter
@JsonDeserialize(using = EjoinPortStateDeserializer.class)
public enum EjoinPortState {
    NO_SIM_CARD(0),
    IDLE(1),
    REGISTERING(2),
    REGISTERED(3),
    CALL_CONNECTED(4),
    NO_BALANCE_OR_ALARM(5),
    REGISTER_FAILED(6),
    LOCKED_BY_DEVICE(7),
    LOCKED_BY_OPERATOR(8),
    SIM_ERROR(9),
    CARD_DETECTED(11),
    USER_LOCKED(12),
    INTER_CALLING(13),
    INTER_CALLING_HOLDING(14),
    ACCESS_MOBILE_NETWORK(15),
    MODULE_TIMEOUT(16);

    private final int code;

    EjoinPortState(int code) { this.code = code; }

}