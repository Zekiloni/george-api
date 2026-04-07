package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je invoice u potpunosti plaćen i završen.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceSettledEvent extends InvoiceEvent {
    private boolean manuallyMarked;
    private boolean overPaid;

    @Builder
    public InvoiceSettledEvent(
            String deliveryId,
            String webhookId,
            String originalDeliveryId,
            boolean isRedelivery,
            String storeId,
            String invoiceId,
            java.time.OffsetDateTime timestamp,
            java.util.Map<String, Object> metadata,
            boolean manuallyMarked,
            boolean overPaid) {
        super(InvoiceEventType.InvoiceSettled);
        this.deliveryId = deliveryId;
        this.webhookId = webhookId;
        this.originalDeliveryId = originalDeliveryId;
        this.isRedelivery = isRedelivery;
        this.storeId = storeId;
        this.invoiceId = invoiceId;
        this.timestamp = timestamp;
        this.metadata = metadata;
        this.manuallyMarked = manuallyMarked;
        this.overPaid = overPaid;
    }

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceSettledEvent");
        }
    }
}

