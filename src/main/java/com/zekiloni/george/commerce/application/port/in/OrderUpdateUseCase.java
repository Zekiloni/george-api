package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.order.model.Order;

public interface OrderUpdateUseCase {
    Order update(Order order);
}

