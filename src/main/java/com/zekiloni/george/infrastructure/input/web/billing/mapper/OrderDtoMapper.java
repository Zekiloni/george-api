package com.zekiloni.george.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.domain.billing.model.Order;
import com.zekiloni.george.infrastructure.input.web.billing.dto.OrderCreateDto;
import com.zekiloni.george.infrastructure.input.web.billing.dto.OrderDto;
import org.mapstruct.Mapper;

@Mapper
public interface OrderDtoMapper {
    Order toDomain(OrderCreateDto orderCreate);

    OrderDto toDto(Order order);
}

