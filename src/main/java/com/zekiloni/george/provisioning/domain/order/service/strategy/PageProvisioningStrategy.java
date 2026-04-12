package com.zekiloni.george.provisioning.domain.order.service.strategy;

import com.zekiloni.george.provisioning.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.inventory.model.PageServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
        ServiceAccess serviceAccess = createServiceAccess(order, orderItem);
        serviceAccessCreateUseCase.create(serviceAccess);
    }

    @Override
    public void deprovision(OrderItem order) {

    }

    private ServiceAccess createServiceAccess(Order order, OrderItem orderItem) {
        return PageServiceAccess.builder()
                .serviceSpecification(getType())
                .orderItem(orderItem)
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .tenantId(order.getTenantId())
                .build();
    }
}
