package com.zekiloni.george.application.port.in;

import com.zekiloni.george.domain.billing.model.Order;

public interface OrderCreateUseCase {
    Order create(Order orderCreateCommand);
}

