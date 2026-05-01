package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.commerce.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.OrderSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderQueryService implements OrderQueryUseCase {
    private final OrderRepositoryPort repository;

    @Override
    public Optional<Order> getById(String orderId) {
        return repository.findById(orderId);
    }

    @Override
    public Page<Order> getAll(Pageable pageable, OrderSpecification specification) {
        return repository.findAll(pageable, specification);
    }
}

