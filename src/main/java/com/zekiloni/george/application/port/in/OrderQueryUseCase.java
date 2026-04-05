package com.zekiloni.george.application.port.in;

import com.zekiloni.george.domain.billing.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderQueryUseCase {
    Optional<Order> getById(String orderId);
    Page<Order> getAll(Pageable pageable);
}

