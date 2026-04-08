package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.provisioning.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceAccessCreateService  implements ServiceAccessCreateUseCase {
    private final InventoryRepositoryPort repository;

    @Override
    public List<ServiceAccess> create(List<ServiceAccess> serviceAccesses) {
        return repository.saveAll(serviceAccesses);
    }

    @Override
    public ServiceAccess create(ServiceAccess serviceAccess) {
        return repository.save(serviceAccess);
    }
}
