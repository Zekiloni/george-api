package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceInvalidEvent;
import org.springframework.stereotype.Component;

@Component
public class InvoiceInvalidEventHandler implements InvoiceEventHandler<InvoiceInvalidEvent> {
    @Override
    public Class<InvoiceInvalidEvent> getEventType() {
        return InvoiceInvalidEvent.class;
    }

    @Override
    public void handle(InvoiceInvalidEvent event) {

    }
}
