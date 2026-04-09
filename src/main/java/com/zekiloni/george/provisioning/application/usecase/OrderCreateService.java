package com.zekiloni.george.provisioning.application.usecase;

import com.zekiloni.george.provisioning.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.provisioning.application.port.in.OrderCreateUseCase;
import com.zekiloni.george.provisioning.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreateService implements OrderCreateUseCase {
    private final OrderRepositoryPort repository;
    private final InvoiceCreateUseCase invoiceCreateUseCase;

    @Override
    public Order create(Order orderCreate) {
        orderCreate.setStatus(OrderStatus.ACKNOWLEDGED);
        Order newOrder = repository.save(orderCreate);
        invoiceCreateUseCase.create(newOrder);
        return newOrder;
    }


}

