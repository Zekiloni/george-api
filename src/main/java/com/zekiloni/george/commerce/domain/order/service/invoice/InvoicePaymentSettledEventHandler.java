package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoicePaymentSettledEvent;
import org.springframework.stereotype.Component;

@Component
public class InvoicePaymentSettledEventHandler implements InvoiceEventHandler<InvoicePaymentSettledEvent> {

    @Override
    public Class<InvoicePaymentSettledEvent> getEventType() {
        return InvoicePaymentSettledEvent.class;
    }

    @Override
    public void handle(InvoicePaymentSettledEvent event) {

    }
}
