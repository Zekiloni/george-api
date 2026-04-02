package com.zekiloni.george.domain.form.entity;

/**
 * Enum representing validation types that can be applied to form fields.
 */
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
    CREDIT_CARD("credit_card"),
    DATE_FORMAT("date_format"),
    CUSTOM("custom");

    private final String value;

    ValidationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

