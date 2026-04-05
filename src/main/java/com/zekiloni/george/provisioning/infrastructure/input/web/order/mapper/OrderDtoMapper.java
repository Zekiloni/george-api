package com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.OrderCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper
public interface OrderDtoMapper {
    Order toDomain(OrderCreateDto orderCreate);

    OrderDto toDto(Order order);
}

