package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InvoiceQueryUseCase {
    Optional<Invoice> getByOrderId(String orderId);
    Page<Invoice> getAll(Pageable pageable, InvoiceSpecification specification);
}
