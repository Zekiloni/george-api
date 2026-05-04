package com.zekiloni.george.commerce.application.port.out;

import com.zekiloni.george.commerce.domain.inventory.model.ServiceUsage;

import java.util.Optional;

public interface ServiceUsageRepositoryPort {
    ServiceUsage save(ServiceUsage usage);

    Optional<ServiceUsage> findByServiceAccessId(String serviceAccessId);

    /**
     * Pessimistic-lock the row for the duration of the calling transaction.
     * Used by the quota service to atomically reserve/release a batch.
     */
    Optional<ServiceUsage> lockByServiceAccessId(String serviceAccessId);
}
