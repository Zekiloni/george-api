package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je invoice u obradi (za vreme obrade plaćanja).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceProcessingEvent extends InvoiceEvent {
    private boolean overPaid;

    @Builder
    public InvoiceProcessingEvent(
            String deliveryId,
            String webhookId,
            String originalDeliveryId,
            boolean isRedelivery,
            String storeId,
            String invoiceId,
            java.time.OffsetDateTime timestamp,
            java.util.Map<String, Object> metadata,
            boolean overPaid) {
        super(InvoiceEventType.InvoiceProcessing);
        this.deliveryId = deliveryId;
        this.webhookId = webhookId;
        this.originalDeliveryId = originalDeliveryId;
        this.isRedelivery = isRedelivery;
        this.storeId = storeId;
        this.invoiceId = invoiceId;
        this.timestamp = timestamp;
        this.metadata = metadata;
        this.overPaid = overPaid;
    }

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceProcessingEvent");
        }
    }
}

