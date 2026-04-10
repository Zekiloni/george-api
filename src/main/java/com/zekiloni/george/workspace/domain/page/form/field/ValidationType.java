package com.zekiloni.george.workspace.domain.page.form.field;

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
    CREDIT_CARD("credit_card"),
    DATE_FORMAT("date_format"),
    CUSTOM("custom");

    private final String value;
}

