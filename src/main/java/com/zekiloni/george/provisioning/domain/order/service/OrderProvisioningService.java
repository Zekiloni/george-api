package com.zekiloni.george.provisioning.domain.order.service;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import com.zekiloni.george.provisioning.domain.order.service.strategy.ProvisioningStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderProvisioningService {
    private final List<ProvisioningStrategy> provisioningStrategies;

    public void handle(Order order) {
        order.getItem().forEach(orderItem -> {
            provisioningStrategies.stream()
                    .filter(strategy -> isApplicableStrategy(orderItem, strategy))
                    .findFirst()
                    .ifPresent(strategy -> strategy.provision(orderItem));
        });
    }

    private boolean isApplicableStrategy(OrderItem orderItem, ProvisioningStrategy strategy) {
        return strategy.getType() == orderItem.getOffering().getServiceSpecification();
    }
}
