package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessQueryUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("serviceAccessQueryUseCase")
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
    public boolean hasActiveAccess(ServiceSpecification specification) {
        return repository.hasActiveAccess(specification);
    }
}

