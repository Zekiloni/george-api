package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.adapter;

import com.zekiloni.george.provisioning.application.port.out.OrderRepositoryPort;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.mapper.OrderEntityMapper;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.repository.OrderJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderRepositoryPortAdapter implements OrderRepositoryPort {
    private final OrderJpaRepository repository;
    private final OrderEntityMapper mapper;


    @Override
    public Order save(Order order) {
        return mapper.toDomain(repository.save(mapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(String id) {
        return repository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(UUID.fromString(id));
    }

    @Override
    public Page<Order> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }
}
