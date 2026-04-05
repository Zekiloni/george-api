package com.zekiloni.george.application.usecase;

import com.zekiloni.george.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.domain.billing.model.Order;
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
    public Page<Order> getAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}

