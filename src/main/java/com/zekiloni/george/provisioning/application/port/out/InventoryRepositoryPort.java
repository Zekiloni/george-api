package com.zekiloni.george.provisioning.application.port.out;

import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InventoryRepositoryPort {
    Page<ServiceAccess> findAll(Pageable pageable);
}
