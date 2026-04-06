package com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.OrderCreateDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OrderDtoMapper {

    @Mapping(source = "offeringId", target = "offering.id")
    Order toDomain(OrderCreateDto orderCreate);

    OrderDto toDto(Order order);
}

