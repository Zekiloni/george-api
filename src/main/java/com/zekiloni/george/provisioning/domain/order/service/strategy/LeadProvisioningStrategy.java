package com.zekiloni.george.provisioning.domain.order.service.strategy;

import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.order.model.Order;

public class LeadProvisioningStrategy implements ProvisioningStrategy {

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.LEADS;
    }

    @Override
    public void provision(Order order) {

    }

    @Override
    public void deprovision(Order order) {

    }
}
