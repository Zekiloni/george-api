package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.commerce.application.port.in.OfferingQueryUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderCreateUseCase;
import com.zekiloni.george.commerce.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.model.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCreateService implements OrderCreateUseCase {
    private final OrderRepositoryPort repository;
    private final InvoiceCreateUseCase invoiceCreateUseCase;
    private final OfferingQueryUseCase offeringQueryUseCase;

    @Override
    public Order create(Order orderCreate) {
        validate(orderCreate);
        orderCreate.setStatus(OrderStatus.ACKNOWLEDGED);
        Order newOrder = repository.save(orderCreate);
        invoiceCreateUseCase.create(newOrder);
        return newOrder;
    }

    private void validate(Order orderCreate) {
        orderCreate.getItem().forEach(this::validateItem);
        if (orderCreate.getItem() == null || orderCreate.getItem().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }
    }

    private void validateItem(OrderItem orderItem) {
        Offering offering = offeringQueryUseCase.getById(orderItem.getOffering().getId())
                .orElseThrow(() -> new RuntimeException("order item not found"));

        orderItem.setOffering(offering);

        if (!offering.getStatus().equals(OfferingStatus.ACTIVE)) {
            throw new IllegalArgumentException("Offering with id " + offering.getId() + " is not active");
        }

        if (offering.getBillingConfig().isQuantityAllowed()) {
            if (orderItem.getQuantity() == null || orderItem.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for offering with id " + offering.getId());
            }

            if (offering.getBillingConfig().getMaxQuantity() != null && orderItem.getQuantity() > offering.getBillingConfig().getMaxQuantity()) {
                throw new IllegalArgumentException("Quantity must be less than or equal to " + offering.getBillingConfig().getMaxQuantity() + " for offering with id " + offering.getId());
            }
        }

        if (offering.getBillingConfig().isDurationAllowed()) {
            if (orderItem.getDuration() == null) {
                throw new IllegalArgumentException("Duration must be greater than 0 for offering with id " + offering.getId());
            }
        }
    }
}

