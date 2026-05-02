package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCreateService implements OrderCreateUseCase {
    private final OrderRepositoryPort repository;
    private final OrderValidator validator;
    private final InvoiceCreateUseCase invoiceCreateUseCase;

    @Override
    @Transactional
    public Order create(Order orderCreate) {
        validator.validate(orderCreate);
        orderCreate.setStatus(OrderStatus.ACKNOWLEDGED);
        Order newOrder = repository.save(orderCreate);
        invoiceCreateUseCase.create(newOrder);
        return newOrder;
    }
}
