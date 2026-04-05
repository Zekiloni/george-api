package com.zekiloni.george.workspace.domain.campaign.form.entity;

/**
 * Enum representing different types of form fields.
 * Includes basic types, advanced preset types, and complex types.
 */
public enum FieldType {
    // Basic types
    TEXT("text"),
    EMAIL("email"),
    NUMBER("number"),
    CHECKBOX("checkbox"),
    RADIO("radio"),
    SELECT("select"),
    TEXTAREA("textarea"),
    DATE("date"),
    TIME("time"),
    DATETIME("datetime"),
    FILE("file"),
    HIDDEN("hidden"),

    // Advanced preset types
    PASSWORD("password"),
    ADDRESS("address"),
    PHONE_NUMBER("phone_number"),
    CREDIT_CARD("credit_card"),
    ZIP_CODE("zip_code"),
    URL("url"),
    CURRENCY("currency"),
    PERCENTAGE("percentage"),
    COLOR_PICKER("color_picker"),
    MULTI_SELECT("multi_select"),
    RATING("rating"),
    SIGNATURE("signature"),
    REPEAT("repeat");

    private final String value;

    FieldType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

