package com.zekiloni.george.domain.gsm.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Enum representing GSM Box connection status.
 */
@Getter
@RequiredArgsConstructor
public enum GSMBoxStatus {
    ONLINE("online", "Online"),
    OFFLINE("offline", "Offline"),
    IDLE("idle", "Idle"),
    BUSY("busy", "Busy"),
    ERROR("error", "Error"),
    MAINTENANCE("maintenance", "Maintenance"),
    DISCONNECTED("disconnected", "Disconnected"),
    INITIALIZING("initializing", "Initializing");

    private final String value;
    private final String displayName;
}

