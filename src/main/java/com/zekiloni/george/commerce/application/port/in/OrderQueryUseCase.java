package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderQueryUseCase {
    Optional<Order> getById(String orderId);
    Page<Order> getAll(Pageable pageable, OrderSpecification specification);
}

