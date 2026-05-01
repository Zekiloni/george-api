package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.order.model.Order;

public interface OrderCancelUseCase {
    Order cancel(String orderId);
}
