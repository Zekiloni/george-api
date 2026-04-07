package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je invoice označen kao nevažeći.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceInvalidEvent extends InvoiceEvent {
    private boolean manuallyMarked;

    @Builder
    public InvoiceInvalidEvent(
            String deliveryId,
            String webhookId,
            String originalDeliveryId,
            boolean isRedelivery,
            String storeId,
            String invoiceId,
            java.time.OffsetDateTime timestamp,
            java.util.Map<String, Object> metadata,
            boolean manuallyMarked) {
        super(InvoiceEventType.InvoiceInvalid);
        this.deliveryId = deliveryId;
        this.webhookId = webhookId;
        this.originalDeliveryId = originalDeliveryId;
        this.isRedelivery = isRedelivery;
        this.storeId = storeId;
        this.invoiceId = invoiceId;
        this.timestamp = timestamp;
        this.metadata = metadata;
        this.manuallyMarked = manuallyMarked;
    }

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceInvalidEvent");
        }
    }
}

