package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.repository;

import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
}
