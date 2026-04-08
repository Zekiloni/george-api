package com.zekiloni.george.provisioning.domain.order.service.strategy;

import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;

public interface ProvisioningStrategy {
    ServiceSpecification getType();

    void provision(Order order, OrderItem orderItem);

    void deprovision(OrderItem order);
}