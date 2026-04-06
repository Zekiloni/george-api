package com.zekiloni.george.common.domain.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CharacteristicValueType {
    STRING("STRING"),
    INTEGER("INTEGER"),
    DOUBLE("DOUBLE"),
    BOOLEAN("BOOLEAN");

    private final String value;

    @JsonCreator
    public static CharacteristicValueType fromString(String value) {
        for (CharacteristicValueType enumValue : CharacteristicValueType.values()) {
            if (enumValue.getValue().equalsIgnoreCase(value)) {
                return enumValue;
            }
        }
        return null;
    }
}

