package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.service.strategy.ProvisioningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServiceAccessTerminator {

    private final InventoryRepositoryPort repository;
    private final Map<ServiceSpecification, ProvisioningStrategy> strategiesByType;

    public ServiceAccessTerminator(InventoryRepositoryPort repository,
                                   List<ProvisioningStrategy> provisioningStrategies) {
        this.repository = repository;
        this.strategiesByType = provisioningStrategies.stream()
                .collect(Collectors.toMap(ProvisioningStrategy::getType, Function.identity()));
    }

    @Transactional
    public void terminate(ServiceAccess access) {
        if (access.getStatus() == ServiceStatus.TERMINATED) {
            return;
        }

        ProvisioningStrategy strategy = strategiesByType.get(access.getServiceSpecification());
        if (strategy != null) {
            strategy.deprovision(access);
        } else {
            log.warn("No provisioning strategy registered for {}; terminating without remote cleanup",
                    access.getServiceSpecification());
        }

        access.setStatus(ServiceStatus.TERMINATED);
        access.setUpdatedAt(OffsetDateTime.now());
        repository.save(access);
    }
}
