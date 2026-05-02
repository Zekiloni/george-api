package com.zekiloni.george.commerce.infrastructure.out.persistence.order.repository;

import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceJpaRepository extends JpaRepository<InvoiceEntity, UUID>, JpaSpecificationExecutor<InvoiceEntity> {
    Optional<InvoiceEntity> findByOrderId(UUID orderId);

    Optional<InvoiceEntity> findByInvoiceNumber(String invoiceNumber);

    @Query("""
        SELECT CASE WHEN COUNT(i) > 0 THEN true ELSE false END
        FROM InvoiceEntity i
        WHERE i.serviceAccessId = :serviceAccessId
          AND i.status = com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceStatus.PENDING
        """)
    boolean existsOpenRenewalForServiceAccess(@Param("serviceAccessId") String serviceAccessId);
}
