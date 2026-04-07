package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je invoice kreiran.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceCreatedEvent extends InvoiceEvent {

    @Builder
    public InvoiceCreatedEvent(
            String deliveryId,
            String webhookId,
            String originalDeliveryId,
            boolean isRedelivery,
            String storeId,
            String invoiceId,
            java.time.OffsetDateTime timestamp,
            java.util.Map<String, Object> metadata) {
        super(InvoiceEventType.InvoiceCreated);
        this.deliveryId = deliveryId;
        this.webhookId = webhookId;
        this.originalDeliveryId = originalDeliveryId;
        this.isRedelivery = isRedelivery;
        this.storeId = storeId;
        this.invoiceId = invoiceId;
        this.timestamp = timestamp;
        this.metadata = metadata;
    }

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceCreatedEvent");
        }
    }

    @Override
    public <T> T accept(InvoiceEventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

