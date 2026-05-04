package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.adapter;

import com.zekiloni.george.commerce.application.port.out.ServiceUsageRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceUsage;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.mapper.ServiceUsageEntityMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.repository.ServiceUsageJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ServiceUsageRepositoryPortAdapter implements ServiceUsageRepositoryPort {
    private final ServiceUsageJpaRepository repository;
    private final ServiceUsageEntityMapper mapper;

    @Override
    public ServiceUsage save(ServiceUsage usage) {
        return mapper.toDomain(repository.save(mapper.toEntity(usage)));
    }

    @Override
    public Optional<ServiceUsage> findByServiceAccessId(String serviceAccessId) {
        return repository.findByServiceAccessId(serviceAccessId).map(mapper::toDomain);
    }

    @Override
    public Optional<ServiceUsage> lockByServiceAccessId(String serviceAccessId) {
        return repository.lockByServiceAccessId(serviceAccessId).map(mapper::toDomain);
    }
}
