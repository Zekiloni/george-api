package com.zekiloni.george.provisioning.infrastructure.out.persistence.order.repository;

import com.zekiloni.george.provisioning.infrastructure.out.persistence.order.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID>, JpaSpecificationExecutor<InvoiceEntity> {
    Optional<InvoiceEntity> findByOrderId(UUID orderId);
}
