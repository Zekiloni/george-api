package com.zekiloni.george.platform.application.port.out.gateway;

import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface GatewayRepositoryPort {
    Gateway save(Gateway gateway);
    Gateway update(Gateway gateway);
    void deleteById(String id);
    Optional<Gateway> findById(String id);
    List<Gateway> findByType(GatewayType type);
    List<Gateway> findAll();
    Page<Gateway> findAll(Pageable pageable);
    List<Gateway> findActive();
    boolean existsById(String id);
}
