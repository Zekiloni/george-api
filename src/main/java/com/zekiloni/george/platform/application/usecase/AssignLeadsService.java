package com.zekiloni.george.platform.application.usecase;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.platform.application.port.in.AssignLeadsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssignLeadsService implements AssignLeadsUseCase {
    private final InventoryRepositoryPort repository;

    @Override
    public void handle(String serviceAccessId, List<String> leadIds) {
        ServiceAccess serviceAccess = repository.findById(serviceAccessId)
                .orElseThrow(() -> new IllegalArgumentException("ServiceAccess not found: " + serviceAccessId));

        if (!(serviceAccess instanceof LeadServiceAccess leadServiceAccess)) {
            throw new IllegalStateException("ServiceAccess does not support lead assignment: " + serviceAccessId);
        }

        leadServiceAccess.addLeads(leadIds);
        repository.save(leadServiceAccess);
    }
}
