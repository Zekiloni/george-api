package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;

import java.time.OffsetDateTime;

public interface ProvisioningStrategy {
    ServiceSpecification getType();

    void provision(Order order, OrderItem orderItem);

    void deprovision(ServiceAccess access);

    default OffsetDateTime getValidTo(OrderItem orderItem) {
        OffsetDateTime now = OffsetDateTime.now();
        return switch (orderItem.getDurationUnit()) {
            case HOURS -> now.plusHours(orderItem.getDuration());
            case DAYS -> now.plusDays(orderItem.getDuration());
            case MONTHS -> now.plusMonths(orderItem.getDuration());
            case YEARS -> now.plusYears(orderItem.getDuration());
            default -> throw new IllegalArgumentException("Unsupported duration unit: " + orderItem.getDurationUnit());
        };
    }
}