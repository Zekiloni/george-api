package com.zekiloni.george.provisioning.domain.order.service.strategy;

import com.zekiloni.george.provisioning.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SmtProvisioningStrategy implements ProvisioningStrategy{
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.SMTP;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        // todo: contact SMT provider to provision the service access for the order item
        SmtpServiceAccess build = SmtpServiceAccess.builder().build();
        serviceAccessCreateUseCase.create(build);
    }

    @Override
    public void deprovision(OrderItem order) {

    }
}
