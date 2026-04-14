package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.ServiceAccessQueryUseCase;
import com.zekiloni.george.provisioning.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceAccessQueryService implements ServiceAccessQueryUseCase {
    private final InventoryRepositoryPort repository;

    @Override
    public Optional<ServiceAccess> getById(String id) {
        return repository.findById(id);
    }

    @Override
    public Page<ServiceAccess> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public boolean hasActiveAccess(com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification specification) {
        return repository.hasActiveAccess(specification);
    }
}

