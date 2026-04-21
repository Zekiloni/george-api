package com.zekiloni.george.commerce.application.usecase;


import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ServiceAccessCreateService  implements ServiceAccessCreateUseCase {
    private final InventoryRepositoryPort repository;

    @Override
    public ServiceAccess create(ServiceAccess serviceAccess) {
        return repository.save(serviceAccess);
    }
}
