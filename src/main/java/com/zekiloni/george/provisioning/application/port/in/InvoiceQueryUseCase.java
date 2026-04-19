package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.entity.InvoiceSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InvoiceQueryUseCase {
    Optional<Invoice> getByOrderId(String orderId);
    Page<Invoice> getAll(Pageable pageable, InvoiceSpecification specification);
}
