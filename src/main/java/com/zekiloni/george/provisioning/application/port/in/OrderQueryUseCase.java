package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderQueryUseCase {
    Optional<Order> getById(String orderId);
    Page<Order> getAll(Pageable pageable);
}

