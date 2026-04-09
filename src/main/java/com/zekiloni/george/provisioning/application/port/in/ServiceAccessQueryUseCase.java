package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ServiceAccessQueryUseCase {
    Optional<ServiceAccess> getById(String id);
    Page<ServiceAccess> getAll(Pageable pageable);
}

