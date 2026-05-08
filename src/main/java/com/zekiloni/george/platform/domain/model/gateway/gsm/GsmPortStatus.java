package com.zekiloni.george.platform.domain.model.gateway.gsm;

public enum GsmPortStatus {
    NO_SIM,
    IDLE,
    REGISTERING,
    REGISTERED,
    IN_CALL,
    ERROR,
    UNKNOWN;

    public boolean readyToSend() {
        return this == REGISTERED || this == IDLE;
    }
}
