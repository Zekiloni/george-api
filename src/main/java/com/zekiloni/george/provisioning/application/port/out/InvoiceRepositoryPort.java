package com.zekiloni.george.provisioning.application.port.out;

import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;

import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepositoryPort {

    Optional<Invoice> findByOrderId(UUID orderId);

    Invoice save(Invoice invoice);
}
