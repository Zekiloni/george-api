package com.zekiloni.george.platform.domain.model.gateway;

import java.util.Map;

/**
 * Stable keys used inside Gateway.config so adapters don't sprinkle string literals.
 * Each provider enum declares which subset of these it expects.
 */
public final class GatewayConfigKeys {
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String USE_TLS = "useTls";
    public static final String FROM_DOMAIN = "fromDomain";
    public static final String ADMIN_URL = "adminUrl";
    public static final String IP_ADDRESS = "ipAddress";
    public static final String TOTAL_PORT = "totalPort";

    public static final String API_KEY = "apiKey";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String SMTP_USERNAME = "smtpUsername";
    public static final String SMTP_PASSWORD = "smtpPassword";

    public static final String REDACTED = "***";

    private GatewayConfigKeys() {}

    public static String string(Map<String, String> config, String key) {
        return config == null ? null : config.get(key);
    }

    public static int intValue(Map<String, String> config, String key, int fallback) {
        String v = string(config, key);
        if (v == null || v.isBlank()) return fallback;
        try { return Integer.parseInt(v.trim()); } catch (NumberFormatException e) { return fallback; }
    }

    public static boolean boolValue(Map<String, String> config, String key, boolean fallback) {
        String v = string(config, key);
        return v == null ? fallback : Boolean.parseBoolean(v.trim());
    }
}
