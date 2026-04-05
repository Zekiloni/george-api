package com.zekiloni.george.provisioning.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.OrderCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper
public interface OrderDtoMapper {
    Order toDomain(OrderCreateDto orderCreate);

    OrderDto toDto(Order order);
}

