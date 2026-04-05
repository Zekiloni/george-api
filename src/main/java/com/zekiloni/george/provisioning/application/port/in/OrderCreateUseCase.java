package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.billing.model.Order;

public interface OrderCreateUseCase {
    Order create(Order orderCreateCommand);
}

