package com.zekiloni.george.commerce.domain.inventory.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServiceAccessCharacteristicKey {
    COUNTRY("country"),
    DAILY_LIMIT("dailyLimit"),
    MESSAGE_QUOTA("messageQuota");

    private final String value;
}
