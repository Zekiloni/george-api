package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.OrderCancelUseCase;
import com.zekiloni.george.commerce.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class OrderCancelService implements OrderCancelUseCase {
    private final OrderRepositoryPort repository;

    @Override
    @Transactional
    public Order cancel(String orderId) {
        Order order = repository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found: " + orderId));

        if (order.getStatus() == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel a completed order");
        }
        if (order.getStatus() == OrderStatus.CANCELED) {
            return order;
        }
        order.setStatus(OrderStatus.CANCELED);
        return repository.save(order);
    }
}
