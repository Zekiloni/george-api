package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.PageServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class PageProvisioningStrategy implements ProvisioningStrategy {
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.PAGE;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        PageServiceAccess serviceAccess = createServiceAccess(order, orderItem);
        serviceAccessCreateUseCase.create(serviceAccess);
    }

    @Override
    public void deprovision(ServiceAccess access) {
    }

    private PageServiceAccess createServiceAccess(Order order, OrderItem orderItem) {
        return PageServiceAccess.builder()
                .serviceSpecification(getType())
                .orderItem(orderItem)
                .status(ServiceStatus.ACTIVE)
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .characteristic(orderItem.getCharacteristic())
                .tenantId(order.getTenantId())
                .build();
    }
}
