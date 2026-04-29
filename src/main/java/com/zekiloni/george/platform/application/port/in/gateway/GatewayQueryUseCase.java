package com.zekiloni.george.platform.application.port.in.gateway;

import com.zekiloni.george.platform.domain.model.gatway.Gateway;
import com.zekiloni.george.platform.domain.model.gatway.GatewayType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GatewayQueryUseCase {
    Gateway findById(String id);
    List<Gateway> findByType(GatewayType type);
    List<Gateway> findAll();
    Page<Gateway> findAll(Pageable pageable);
    List<Gateway> findActive();
    Gateway findLeastLoaded(GatewayType type);
}
