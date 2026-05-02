package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessLifecycleUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.order.service.strategy.ProvisioningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Walks ServiceAccess lifecycle:
 *  - ACTIVE with {@code validTo <= now} → suspend (via strategy hook + status=SUSPENDED).
 *  - SUSPENDED with {@code suspendedAt + gracePeriodDays <= now} → terminate.
 *
 * Per-offering {@code gracePeriodDays} on the offering overrides the platform default
 * from {@link LifecycleConfig}.
 */
@Service
@Slf4j
public class ServiceAccessLifecycleService implements ServiceAccessLifecycleUseCase {

    private final InventoryRepositoryPort repository;
    private final ServiceAccessTerminator terminator;
    private final LifecycleConfig lifecycleConfig;
    private final Map<ServiceSpecification, ProvisioningStrategy> strategiesByType;

    public ServiceAccessLifecycleService(InventoryRepositoryPort repository,
                                         ServiceAccessTerminator terminator,
                                         LifecycleConfig lifecycleConfig,
                                         List<ProvisioningStrategy> provisioningStrategies) {
        this.repository = repository;
        this.terminator = terminator;
        this.lifecycleConfig = lifecycleConfig;
        this.strategiesByType = provisioningStrategies.stream()
                .collect(Collectors.toMap(ProvisioningStrategy::getType, Function.identity()));
    }

    @Override
    @Transactional
    public void handle() {
        OffsetDateTime now = OffsetDateTime.now();

        suspendExpired(now);
        terminateAfterGrace(now);
    }

    private void suspendExpired(OffsetDateTime now) {
        List<ServiceAccess> expired = repository.findExpired(ServiceStatus.ACTIVE, now);
        for (ServiceAccess access : expired) {
            try {
                ProvisioningStrategy strategy = strategiesByType.get(access.getServiceSpecification());
                if (strategy != null) strategy.suspend(access);

                access.setStatus(ServiceStatus.SUSPENDED);
                access.setSuspendedAt(now);
                access.setUpdatedAt(now);
                repository.save(access);

                log.info("Suspended ServiceAccess {} ({})", access.getId(), access.getServiceSpecification());
            } catch (Exception e) {
                log.error("Suspension failed for access {}: {}", access.getId(), e.getMessage(), e);
            }
        }
    }

    private void terminateAfterGrace(OffsetDateTime now) {
        List<ServiceAccess> suspended = repository.findExpired(ServiceStatus.SUSPENDED, now);
        for (ServiceAccess access : suspended) {
            try {
                if (!gracePeriodElapsed(access, now)) continue;
                terminator.terminate(access);
            } catch (Exception e) {
                log.error("Termination failed for access {}: {}", access.getId(), e.getMessage(), e);
            }
        }
    }

    private boolean gracePeriodElapsed(ServiceAccess access, OffsetDateTime now) {
        OffsetDateTime suspendedAt = access.getSuspendedAt();
        if (suspendedAt == null) return true;  // missing data — fall through to terminate
        Offering offering = access.getOrderItem() != null ? access.getOrderItem().getOffering() : null;
        int graceDays = (offering != null && offering.getGracePeriodDays() != null)
                ? offering.getGracePeriodDays()
                : lifecycleConfig.getGracePeriodDays();
        return !suspendedAt.plusDays(graceDays).isAfter(now);
    }
}
