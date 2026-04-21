package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import com.zekiloni.george.common.domain.model.DomainEvent;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * Apstraktna bazna klasa za sve invoice evente.
 * Sadrži zajedničke podatke za sve invoice evente.
 */
@Data
@SuperBuilder
@NoArgsConstructor
public abstract class InvoiceEvent implements DomainEvent {
    public static final String ORDER_ID = "orderId";

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

    public String getOrderId() {
        return metadata != null ? (String) metadata.get(ORDER_ID) : null;
    }

    /**
     * Metoda koja omogućava validaciju specifičnog eventa
     */
    public abstract void validate();
}

