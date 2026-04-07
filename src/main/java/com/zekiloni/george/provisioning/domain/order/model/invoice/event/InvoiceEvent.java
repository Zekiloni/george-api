package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import com.zekiloni.george.common.domain.model.DomainEvent;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Apstraktna bazna klasa za sve invoice evente.
 * Sadrži zajedničke podatke za sve invoice evente.
 */
@Data
public abstract class InvoiceEvent implements DomainEvent {
    protected String deliveryId;
    protected String webhookId;
    protected String originalDeliveryId;
    protected boolean isRedelivery;
    protected InvoiceEventType type;
    protected OffsetDateTime timestamp;
    protected String storeId;
    protected String invoiceId;
    protected Map<String, Object> metadata;

    protected InvoiceEvent(InvoiceEventType type) {
        this.type = type;
    }

    /**
     * Metoda koja omogućava validaciju specifičnog eventa
     */
    public abstract void validate();

    /**
     * Metoda koja omogućava transformaciju eventa
     */
    public abstract <T> T accept(InvoiceEventVisitor<T> visitor);
}

