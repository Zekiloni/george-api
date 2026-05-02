package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessRenewalControlUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ServiceAccessRenewalControlService implements ServiceAccessRenewalControlUseCase {

    private final InventoryRepositoryPort repository;

    @Override
    @Transactional
    public void setCancelAtPeriodEnd(String serviceAccessId, boolean cancelAtPeriodEnd) {
        ServiceAccess access = repository.findById(serviceAccessId)
                .orElseThrow(() -> new NoSuchElementException("ServiceAccess " + serviceAccessId + " not found"));
        access.setCancelAtPeriodEnd(cancelAtPeriodEnd);
        access.setUpdatedAt(OffsetDateTime.now());
        repository.save(access);
    }
}
