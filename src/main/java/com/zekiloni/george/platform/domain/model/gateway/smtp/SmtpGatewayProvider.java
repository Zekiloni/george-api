package com.zekiloni.george.platform.domain.model.gateway.smtp;

import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;

import java.util.Set;

public enum SmtpGatewayProvider {
    STALWART(
            Set.of(GatewayConfigKeys.URL, GatewayConfigKeys.USE_TLS, GatewayConfigKeys.FROM_DOMAIN),
            // apiKey is preferred (Bearer); username+password is the Basic-auth
            // fallback used while bootstrapping with the admin account.
            Set.of(GatewayConfigKeys.API_KEY, GatewayConfigKeys.USERNAME, GatewayConfigKeys.PASSWORD)
    ),
    POSTAL(
            Set.of(GatewayConfigKeys.URL, GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.API_KEY)
    ),
    MAILCOW(
            Set.of(GatewayConfigKeys.URL, GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.API_KEY)
    ),
    SMTP_RELAY(
            Set.of(GatewayConfigKeys.URL, GatewayConfigKeys.USE_TLS, GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.USERNAME, GatewayConfigKeys.PASSWORD)
    ),
    EJOIN(
            Set.of(GatewayConfigKeys.URL, GatewayConfigKeys.USE_TLS, GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.USERNAME, GatewayConfigKeys.PASSWORD)
    );

    private final Set<String> publicKeys;
    private final Set<String> secretKeys;

    SmtpGatewayProvider(Set<String> publicKeys, Set<String> secretKeys) {
        this.publicKeys = publicKeys;
        this.secretKeys = secretKeys;
    }

    public Set<String> publicKeys() { return publicKeys; }
    public Set<String> secretKeys() { return secretKeys; }
}
