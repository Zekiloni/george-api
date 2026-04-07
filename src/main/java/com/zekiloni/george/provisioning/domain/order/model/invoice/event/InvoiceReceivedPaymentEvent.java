package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je primljena uplata na invoice.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceReceivedPaymentEvent extends PaymentEvent {

    @Builder
    public InvoiceReceivedPaymentEvent(
            String deliveryId,
            String webhookId,
            String originalDeliveryId,
            boolean isRedelivery,
            String storeId,
            String invoiceId,
            java.time.OffsetDateTime timestamp,
            java.util.Map<String, Object> metadata,
            boolean afterExpiration,
            String paymentMethodId,
            PaymentDetail payment) {
        super(InvoiceEventType.InvoiceReceivedPayment);
        this.deliveryId = deliveryId;
        this.webhookId = webhookId;
        this.originalDeliveryId = originalDeliveryId;
        this.isRedelivery = isRedelivery;
        this.storeId = storeId;
        this.invoiceId = invoiceId;
        this.timestamp = timestamp;
        this.metadata = metadata;
        this.afterExpiration = afterExpiration;
        this.paymentMethodId = paymentMethodId;
        this.payment = payment;
    }

    @Override
    public <T> T accept(InvoiceEventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

