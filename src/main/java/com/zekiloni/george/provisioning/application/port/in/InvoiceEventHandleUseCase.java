package com.zekiloni.george.provisioning.application.port.in;

import com.zekiloni.george.provisioning.domain.order.model.invoice.event.InvoiceEvent;

public interface InvoiceEventHandleUseCase {
    void handle(InvoiceEvent event);
}
