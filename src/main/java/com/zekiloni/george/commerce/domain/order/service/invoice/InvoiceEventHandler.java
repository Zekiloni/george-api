package com.zekiloni.george.commerce.domain.order.service.invoice;

import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceEvent;

public interface InvoiceEventHandler<T extends InvoiceEvent> {
    Class<T> getEventType();
    void handle(T event);
}
