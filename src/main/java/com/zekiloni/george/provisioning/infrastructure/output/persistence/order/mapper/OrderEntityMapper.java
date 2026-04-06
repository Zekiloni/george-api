package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity.OrderEntity;
import org.mapstruct.Mapper;

@Mapper
public interface OrderEntityMapper {
    OrderEntity toEntity(Order order);

    Order toDomain(OrderEntity orderEntity);
}
