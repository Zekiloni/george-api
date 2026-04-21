package com.zekiloni.george.commerce.infrastructure.out.persistence.order.mapper;

import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.mapper.OfferingEntityMapper;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.OrderEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.OrderItemEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {OfferingEntityMapper.class})
public interface OrderEntityMapper {
    OrderEntity toEntity(Order order);
    OrderItemEntity toEntity(OrderItem orderItem);

    Order toDomain(OrderEntity order);
    OrderItem toDomain(OrderItemEntity orderItem);
}
