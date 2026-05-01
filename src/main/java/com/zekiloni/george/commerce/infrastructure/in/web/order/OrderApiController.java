package com.zekiloni.george.commerce.infrastructure.in.web.order;

import com.zekiloni.george.commerce.application.port.in.OrderCancelUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderCreateUseCase;
import com.zekiloni.george.commerce.application.port.in.OrderQueryUseCase;
import com.zekiloni.george.commerce.infrastructure.in.web.order.dto.OrderCreateDto;
import com.zekiloni.george.commerce.infrastructure.in.web.order.dto.OrderDto;
import com.zekiloni.george.commerce.infrastructure.in.web.order.mapper.OrderDtoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.base.path:/api/v1}/order")
@RequiredArgsConstructor
public class OrderApiController {
    private final OrderCreateUseCase createUseCase;
    private final OrderQueryUseCase queryUseCase;
    private final OrderCancelUseCase cancelUseCase;
    private final OrderDtoMapper mapper;

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@RequestBody OrderCreateDto orderCreate) {
        return ResponseEntity.ok(mapper.toDto(createUseCase.create(mapper.toDomain(orderCreate))));
    }

    @GetMapping
    public ResponseEntity<Page<OrderDto>> getOrders(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.getAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String id) {
        return queryUseCase.getById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(cancelUseCase.cancel(id)));
    }
}
