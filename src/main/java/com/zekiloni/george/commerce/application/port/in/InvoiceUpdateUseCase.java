package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;

public interface InvoiceUpdateUseCase {
    Invoice update(Invoice invoice);
}
