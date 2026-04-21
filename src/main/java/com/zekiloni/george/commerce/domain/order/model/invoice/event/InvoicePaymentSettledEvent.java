package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Event koji se emituje kada je plaćanje se obrađuje.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class InvoicePaymentSettledEvent extends PaymentEvent {

    @Builder
    public InvoicePaymentSettledEvent(
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
        super(InvoiceEventType.InvoicePaymentSettled);
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
}

