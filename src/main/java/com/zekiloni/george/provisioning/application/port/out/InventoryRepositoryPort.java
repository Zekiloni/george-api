package com.zekiloni.george.provisioning.application.port.out;

import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface InventoryRepositoryPort {
    ServiceAccess save(ServiceAccess serviceAccess);
    List<ServiceAccess> saveAll(List<ServiceAccess> serviceAccesses);
    Page<ServiceAccess> findAll(Pageable pageable);
    Optional<ServiceAccess> findById(String id);

    boolean hasActiveAccess(ServiceSpecification serviceSpecification);

    int updateToSuspended(
            ServiceStatus currentStatus,
            ServiceStatus newStatus,
            OffsetDateTime now,
            OffsetDateTime gracePeriodEnd
    );

    int updateToTerminated(
            ServiceStatus currentStatus,
            ServiceStatus newStatus,
            OffsetDateTime now,
            OffsetDateTime gracePeriodEnd
    );
}
