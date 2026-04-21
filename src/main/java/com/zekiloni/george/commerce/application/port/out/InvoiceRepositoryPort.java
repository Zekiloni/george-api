package com.zekiloni.george.commerce.application.port.out;

import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {

    Optional<Invoice> findByOrderId(UUID orderId);

    Invoice save(Invoice invoice);

    Page<Invoice> findAll(Pageable pageable, InvoiceSpecification specification);
}
