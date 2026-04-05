package com.zekiloni.george.infrastructure.output.persistence.billing.repository;

import com.zekiloni.george.infrastructure.output.persistence.billing.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
}

