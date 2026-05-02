package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.adapter;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceAccessSpecification;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.mapper.ServiceAccessEntityMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.repository.ServiceAccessJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InventoryRepositoryPortAdapter implements InventoryRepositoryPort {
    private final ServiceAccessJpaRepository repository;
    private final ServiceAccessEntityMapper mapper;

    @Override
    public ServiceAccess save(ServiceAccess serviceAccess) {
        return mapper.toDomain(repository.save(mapper.toEntity(serviceAccess)));
    }

    @Override
    public List<ServiceAccess> saveAll(List<ServiceAccess> serviceAccesses) {
        return repository.saveAll(mapper.toEntity(serviceAccesses)).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Page<ServiceAccess> findAll(ServiceAccessSpecification specification, Pageable pageable) {
        return repository.findAll(specification, pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<ServiceAccess> findById(String id) {
        return repository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    public int updateToSuspended(ServiceStatus currentStatus, ServiceStatus newStatus, OffsetDateTime now, OffsetDateTime gracePeriodEnd) {
        return repository.updateToSuspended(currentStatus, newStatus, now, gracePeriodEnd);
    }

    @Override
    public int updateToTerminated(ServiceStatus currentStatus, ServiceStatus newStatus, OffsetDateTime now, OffsetDateTime gracePeriodEnd) {
        return repository.updateToTerminated(currentStatus, newStatus, now, gracePeriodEnd);
    }

    @Override
    public boolean hasActiveAccess(ServiceSpecification serviceSpecification) {
        return repository.hasActiveAccess(serviceSpecification);
    }

    @Override
    public Set<Integer> findAllocatedGsmPorts(String gatewayId) {
        return repository.findAllocatedGsmPorts(gatewayId);
    }

    @Override
    public List<ServiceAccess> findExpired(ServiceStatus status, OffsetDateTime cutoff) {
        return repository.findAllByStatusAndValidToBefore(status, cutoff).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<ServiceAccess> findRenewable(OffsetDateTime now, int renewalNoticeDays) {
        OffsetDateTime horizon = now.plusDays(Math.max(renewalNoticeDays, 0));
        return repository.findRenewable(now, horizon).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
