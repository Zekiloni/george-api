package com.zekiloni.george.platform.infrastructure.out.persistence.gateway.adapter;

import com.zekiloni.george.platform.application.port.out.gateway.GatewayRepositoryPort;
import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GatewayRepositoryPortAdapter implements GatewayRepositoryPort {
    @Override
    public Optional<Gateway> findById(String id) {
        return Optional.empty();
    }
}
