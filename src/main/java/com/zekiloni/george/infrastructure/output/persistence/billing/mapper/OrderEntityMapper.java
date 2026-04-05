package com.zekiloni.george.infrastructure.output.persistence.billing.mapper;

import com.zekiloni.george.domain.billing.model.Order;
import com.zekiloni.george.infrastructure.output.persistence.billing.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OrderEntityMapper {
    OrderEntity toEntity(Order order);
    Order toDomain(OrderEntity orderEntity);
}

