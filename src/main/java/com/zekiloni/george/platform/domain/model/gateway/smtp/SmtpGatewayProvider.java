package com.zekiloni.george.platform.domain.model.gateway.smtp;

import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;

import java.util.Set;

public enum SmtpGatewayProvider {
    STALWART(
            Set.of(GatewayConfigKeys.HOST, GatewayConfigKeys.PORT, GatewayConfigKeys.USE_TLS,
                    GatewayConfigKeys.FROM_DOMAIN, GatewayConfigKeys.ADMIN_URL),
            Set.of(GatewayConfigKeys.API_KEY)
    ),
    POSTAL(
            Set.of(GatewayConfigKeys.HOST, GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.API_KEY)
    ),
    MAILCOW(
            Set.of(GatewayConfigKeys.HOST, GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.API_KEY)
    ),
    SMTP_RELAY(
            Set.of(GatewayConfigKeys.HOST, GatewayConfigKeys.PORT, GatewayConfigKeys.USE_TLS,
                    GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.SMTP_USERNAME, GatewayConfigKeys.SMTP_PASSWORD)
    ),
    EJOIN(
            Set.of(GatewayConfigKeys.HOST, GatewayConfigKeys.PORT, GatewayConfigKeys.USE_TLS,
                    GatewayConfigKeys.FROM_DOMAIN),
            Set.of(GatewayConfigKeys.SMTP_USERNAME, GatewayConfigKeys.SMTP_PASSWORD)
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
