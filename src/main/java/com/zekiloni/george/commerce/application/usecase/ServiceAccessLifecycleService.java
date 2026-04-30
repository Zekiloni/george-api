package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessLifecycleUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceAccessLifecycleService implements ServiceAccessLifecycleUseCase {

    private static final int GRACE_PERIOD_DAYS = 3;

    private final InventoryRepositoryPort repository;
    private final ServiceAccessTerminator terminator;

    @Override
    @Transactional
    public void handle() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime gracePeriodEnd = now.minusDays(GRACE_PERIOD_DAYS);

        repository.updateToSuspended(ServiceStatus.ACTIVE, ServiceStatus.SUSPENDED, now, gracePeriodEnd);

        List<ServiceAccess> expired = repository.findExpired(ServiceStatus.SUSPENDED, gracePeriodEnd);
        for (ServiceAccess access : expired) {
            try {
                terminator.terminate(access);
            } catch (Exception e) {
                log.error("Termination failed for access {} ({}): {}",
                        access.getId(), access.getServiceSpecification(), e.getMessage(), e);
            }
        }
    }
}
