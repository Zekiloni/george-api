package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceEvent;

public interface InvoiceEventHandleUseCase {
    void handle(InvoiceEvent event);
}
