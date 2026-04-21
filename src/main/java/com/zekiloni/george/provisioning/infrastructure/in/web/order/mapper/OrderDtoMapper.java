package com.zekiloni.george.provisioning.infrastructure.in.web.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import com.zekiloni.george.provisioning.infrastructure.in.web.order.dto.OrderCreateDto;
import com.zekiloni.george.provisioning.infrastructure.in.web.order.dto.OrderDto;
import com.zekiloni.george.provisioning.infrastructure.in.web.order.dto.OrderItemCreateDto;
import com.zekiloni.george.provisioning.infrastructure.in.web.order.dto.OrderItemDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderDtoMapper {
    Order toDomain(OrderCreateDto orderCreate);

    @Mapping(source = "offeringId", target = "offering.id")
    OrderItem toDomain(OrderItemCreateDto orderItemCreate);

    OrderDto toDto(Order order);
    OrderItemDto toDto(OrderItem orderItem);
}

