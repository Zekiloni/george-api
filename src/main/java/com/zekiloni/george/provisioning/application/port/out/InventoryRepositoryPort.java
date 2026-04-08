package com.zekiloni.george.provisioning.application.port.out;

import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryRepositoryPort {
    ServiceAccess save(ServiceAccess serviceAccess);
    List<ServiceAccess> saveAll(List<ServiceAccess> serviceAccesses);
    Page<ServiceAccess> findAll(Pageable pageable);
}
