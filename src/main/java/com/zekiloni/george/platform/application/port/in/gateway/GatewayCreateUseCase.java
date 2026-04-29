package com.zekiloni.george.platform.application.port.in.gateway;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;

public interface GatewayCreateUseCase {
    Gateway create(Gateway gateway);
    Gateway update(Gateway gateway);
    void delete(String id);
    void enable(String id);
    void disable(String id);
}
