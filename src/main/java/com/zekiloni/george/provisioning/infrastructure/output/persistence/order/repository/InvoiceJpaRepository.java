package com.zekiloni.george.provisioning.infrastructure.output.persistence.order.repository;

import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID> {
    Optional<InvoiceEntity> findByOrderId(UUID orderId);
}
