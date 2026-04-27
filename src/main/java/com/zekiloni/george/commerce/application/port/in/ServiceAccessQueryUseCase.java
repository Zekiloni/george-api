package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceAccessSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ServiceAccessQueryUseCase {
    Optional<ServiceAccess> getById(String id);
    Page<ServiceAccess> getAll(ServiceAccessSpecification specification, Pageable pageable);

    boolean hasActiveAccess(ServiceSpecification specification);
}

