package com.zekiloni.george.commerce.domain.order.model;

import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class Order {
    private String id;
    private List<OrderItem> item;
    private OrderStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private Invoice invoice;
    private String tenantId;;
}

