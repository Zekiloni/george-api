package com.zekiloni.george.domain.gsm.entity;

/**
 * Enum representing authentication credential types for GSM boxes.
 */
public enum GSMAuthType {
    BASIC("basic", "Basic Authentication"),
    API_KEY("api_key", "API Key"),
    OAUTH2("oauth2", "OAuth 2.0"),
    BEARER_TOKEN("bearer_token", "Bearer Token"),
    CERTIFICATE("certificate", "Certificate-Based"),
    HMAC("hmac", "HMAC Signature"),
    JWT("jwt", "JSON Web Token"),
    CUSTOM("custom", "Custom Auth");

    private final String value;
    private final String displayName;

    GSMAuthType(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}

