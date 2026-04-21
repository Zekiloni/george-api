package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.OrderUpdateUseCase;
import com.zekiloni.george.commerce.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderUpdateService implements OrderUpdateUseCase {
    private final OrderRepositoryPort repository;

    @Override
    public Order update(Order orderCreate) {
        return repository.save(orderCreate);
    }
}

