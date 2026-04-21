package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessLifecycleUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
@RequiredArgsConstructor
public class ServiceAccessLifecycleService  implements ServiceAccessLifecycleUseCase {
    private static final int GRACE_PERIOD_DAYS = 3;
    private final InventoryRepositoryPort repository;

    @Override
    public void handle() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime gracePeriodEnd = now.minusDays(GRACE_PERIOD_DAYS);
        repository.updateToSuspended(ServiceStatus.ACTIVE, ServiceStatus.SUSPENDED, now, gracePeriodEnd);
        repository.updateToTerminated(ServiceStatus.SUSPENDED, ServiceStatus.TERMINATED, now, gracePeriodEnd);
    }
}
