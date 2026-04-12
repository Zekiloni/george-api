package com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.adapter;

import com.zekiloni.george.provisioning.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.mapper.ServiceAccessEntityMapper;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.repository.ServiceAccessJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
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
    public Page<ServiceAccess> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
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

}
