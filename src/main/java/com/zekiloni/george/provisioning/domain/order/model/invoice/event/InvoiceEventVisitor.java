package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

/**
 * Visitor pattern za obrada različitih tipova invoice eventa.
 * Omogućava fleksibilan pristup različitim event tipovima bez korišćenja instanceof.
 */
public interface InvoiceEventVisitor<T> {
    T visit(InvoiceCreatedEvent event);
    T visit(InvoiceExpiredEvent event);
    T visit(InvoiceReceivedPaymentEvent event);
    T visit(InvoicePaymentSettledEvent event);
    T visit(InvoiceProcessingEvent event);
    T visit(InvoiceInvalidEvent event);
    T visit(InvoiceSettledEvent event);
}

