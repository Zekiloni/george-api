package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;

public interface InvoiceUpdateUseCase {
    Invoice update(Invoice invoice);
}
