package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.OrderCreateUseCase;
import com.zekiloni.george.provisioning.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderStatus;
import com.zekiloni.george.provisioning.infrastructure.integration.btcpay.client.BtcPayApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreateService implements OrderCreateUseCase {
    private final OrderRepositoryPort repository;
    private final BtcPayApiClient btcPayApiClient;

    @Override
    public Order create(Order orderCreate) {
        orderCreate.setStatus(OrderStatus.ACKNOWLEDGED);
        Order order = repository.save(orderCreate);
        btcPayApiClient.createInvoice(order.getId(), "test", "10.00", "USD");
        return order;
    }
}

