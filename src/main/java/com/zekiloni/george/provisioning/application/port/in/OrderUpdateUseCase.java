package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.order.model.Order;

public interface OrderUpdateUseCase {
    Order update(Order order);
}

