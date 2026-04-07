package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;

import java.util.Optional;

public interface InvoiceQueryUseCase {
    Optional<Invoice> getByOrderId(String orderId);
}
