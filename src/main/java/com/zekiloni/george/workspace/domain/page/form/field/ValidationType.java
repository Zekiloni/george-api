package com.zekiloni.george.workspace.domain.page.form.field;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ValidationType {
    REQUIRED("required"),
    MIN_LENGTH("min_length"),
    MAX_LENGTH("max_length"),
    PATTERN("pattern"),
    MIN_VALUE("min_value"),
    MAX_VALUE("max_value"),
    EMAIL("email"),
    URL("url"),
    PHONE("phone"),
    DATE_FORMAT("date_format"),
    CUSTOM("custom");

    private final String value;


    @JsonCreator
    public static ValidationType fromValue(String value) {
        for (ValidationType type : ValidationType.values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown validation type: " + value);
    }
}

