package com.zekiloni.george.commerce.application.port.out;

import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceAccessSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface InventoryRepositoryPort {
    ServiceAccess save(ServiceAccess serviceAccess);
    List<ServiceAccess> saveAll(List<ServiceAccess> serviceAccesses);
    Page<ServiceAccess> findAll(ServiceAccessSpecification specification, Pageable pageable);
    Optional<ServiceAccess> findById(String id);

    boolean hasActiveAccess(ServiceSpecification serviceSpecification);

    Set<Integer> findAllocatedGsmPorts(String gatewayId);

    List<ServiceAccess> findExpired(ServiceStatus status, OffsetDateTime cutoff);

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

    /**
     * Find ACTIVE accesses with {@code validTo} between {@code now} and {@code now + noticeDays},
     * not flagged {@code cancelAtPeriodEnd}, and without a recent open RENEWAL invoice.
     */
    List<ServiceAccess> findRenewable(OffsetDateTime now, int renewalNoticeDays);
}
