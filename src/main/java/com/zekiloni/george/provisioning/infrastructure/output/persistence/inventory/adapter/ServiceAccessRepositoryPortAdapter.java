package com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.adapter;

import com.zekiloni.george.provisioning.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.mapper.ServiceAccessEntityMapper;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.repository.ServiceAccessJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ServiceAccessRepositoryPortAdapter implements InventoryRepositoryPort {
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
}
