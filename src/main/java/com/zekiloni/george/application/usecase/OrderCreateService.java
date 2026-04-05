package com.zekiloni.george.application.usecase;

import com.zekiloni.george.application.port.in.OrderCreateUseCase;
import com.zekiloni.george.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.domain.billing.model.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreateService implements OrderCreateUseCase {
    private final OrderRepositoryPort repository;

    @Override
    public Order create(Order orderCreate) {
        return repository.save(orderCreate);
    }
}

