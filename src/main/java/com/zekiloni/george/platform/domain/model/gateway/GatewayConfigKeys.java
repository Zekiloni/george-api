package com.zekiloni.george.platform.domain.model.gateway;

import java.net.URI;
import java.util.Map;

// Stable keys used inside Gateway.config. Each provider enum's publicKeys()/secretKeys()
// declare which of these it expects.
public final class GatewayConfigKeys {
    public static final String URL = "url";
    public static final String USE_TLS = "useTls";
    public static final String FROM_DOMAIN = "fromDomain";
    public static final String API_KEY = "apiKey";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

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

    public static String urlHost(Map<String, String> config) {
        URI uri = parseUrl(config);
        return uri == null ? null : uri.getHost();
    }

    public static int urlPort(Map<String, String> config, int fallback) {
        URI uri = parseUrl(config);
        if (uri == null) return fallback;
        return uri.getPort() != -1 ? uri.getPort() : fallback;
    }

    private static URI parseUrl(Map<String, String> config) {
        String url = string(config, URL);
        if (url == null || url.isBlank()) return null;
        try { return URI.create(url); } catch (IllegalArgumentException e) { return null; }
    }
}
