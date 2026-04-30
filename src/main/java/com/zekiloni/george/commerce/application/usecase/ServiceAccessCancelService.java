package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessCancelUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServiceAccessCancelService implements ServiceAccessCancelUseCase {

    private final InventoryRepositoryPort repository;
    private final ServiceAccessTerminator terminator;

    @Override
    public void cancel(String serviceAccessId) {
        ServiceAccess access = repository.findById(serviceAccessId)
                .orElseThrow(() -> new NoSuchElementException("Service access not found: " + serviceAccessId));
        terminator.terminate(access);
    }
}
