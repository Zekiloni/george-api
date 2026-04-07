package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je invoice istekao (nije plaćen u vremenu).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceExpiredEvent extends InvoiceEvent {
    private boolean partiallyPaid;

    @Builder
    public InvoiceExpiredEvent(
            String deliveryId,
            String webhookId,
            String originalDeliveryId,
            boolean isRedelivery,
            String storeId,
            String invoiceId,
            java.time.OffsetDateTime timestamp,
            java.util.Map<String, Object> metadata,
            boolean partiallyPaid) {
        super(InvoiceEventType.InvoiceExpired);
        this.deliveryId = deliveryId;
        this.webhookId = webhookId;
        this.originalDeliveryId = originalDeliveryId;
        this.isRedelivery = isRedelivery;
        this.storeId = storeId;
        this.invoiceId = invoiceId;
        this.timestamp = timestamp;
        this.metadata = metadata;
        this.partiallyPaid = partiallyPaid;
    }

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceExpiredEvent");
        }
    }

    @Override
    public <T> T accept(InvoiceEventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

