package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;

import java.util.Optional;

public interface GatewayRepositoryPort {
    Optional<Gateway> findById(String id);
}
