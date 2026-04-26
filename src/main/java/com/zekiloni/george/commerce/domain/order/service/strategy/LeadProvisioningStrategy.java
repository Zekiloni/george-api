package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.platform.application.port.in.lead.LeadQueryUseCase;
import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
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

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.LEADS;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        LeadServiceAccess leadServiceAccess = LeadServiceAccess.builder()
                .serviceSpecification(getType())
                .validFrom(OffsetDateTime.now())
                .leads(getLeads(orderItem))
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .orderItem(orderItem)
                .tenantId(order.getTenantId())
                .build();

        serviceAccessCreateUseCase.create(leadServiceAccess);
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
