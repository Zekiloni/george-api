package com.zekiloni.george.platform.application.usecase;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.LeadServiceAccess;
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
        repository.findById(serviceAccessId)
                .ifPresent(serviceAccess -> {
                    if (serviceAccess instanceof LeadServiceAccess leadServiceAccess) {
                        leadServiceAccess.addLeads(leadIds);
                        repository.save(serviceAccess);
                    }
                });
    }
}
