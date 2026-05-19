package com.zekiloni.george.commerce.domain.order.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class Order {
    private String id;
    private List<OrderItem> item;
    private OrderStatus status;

    private String couponCode;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String tenantId;
}
