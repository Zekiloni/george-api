package com.zekiloni.george.provisioning.domain.order.service.strategy;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.LeadQueryUseCase;
import com.zekiloni.george.platform.domain.model.Lead;
import com.zekiloni.george.provisioning.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class LeadProvisioningStrategy implements ProvisioningStrategy {
    private final LeadQueryUseCase leadQueryUseCase;
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;
    private final TenantContext tenantContext;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.LEADS;
    }

    @Override
    public void provision(Order order, OrderItem item) {
        tenantContext.setTenantId(order.getTenantId());

        try {
            LeadServiceAccess leadServiceAccess = LeadServiceAccess.builder()
                    .serviceSpecification(getType())
                    .validFrom(OffsetDateTime.now())
                    .leads(getLeads(item))
                    .order(order)
                    .build();

            serviceAccessCreateUseCase.create(leadServiceAccess);
        } finally {
            tenantContext.clear();
        }
    }

    @Override
    public void deprovision(OrderItem order) {
        // No deprovisioning needed for leads as they are not consumed or depleted
    }

    private @NonNull List<Lead> getLeads(OrderItem order) {
        return leadQueryUseCase.handle(Pageable.ofSize(order.getQuantity()), null)
                .getContent();
    }
}
