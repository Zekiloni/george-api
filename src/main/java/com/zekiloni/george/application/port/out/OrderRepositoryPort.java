package com.zekiloni.george.application.port.out;

import com.zekiloni.george.domain.billing.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderRepositoryPort {
    Order save(Order order);

    Optional<Order> findById(String id);

    void deleteById(String id);

    Page<Order> findAll(Pageable pageable);
}

