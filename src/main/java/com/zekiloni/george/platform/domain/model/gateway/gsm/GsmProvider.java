package com.zekiloni.george.platform.domain.model.gateway.gsm;

import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;

import java.util.Set;

public enum GsmProvider {
    EJOIN,
    DINSTAR,
    SYNWAY,
    PORTECH;

    private static final Set<String> PUBLIC_KEYS = Set.of(GatewayConfigKeys.URL);
    private static final Set<String> SECRET_KEYS = Set.of(
            GatewayConfigKeys.USERNAME,
            GatewayConfigKeys.PASSWORD
    );

    public Set<String> publicKeys() { return PUBLIC_KEYS; }
    public Set<String> secretKeys() { return SECRET_KEYS; }
}
