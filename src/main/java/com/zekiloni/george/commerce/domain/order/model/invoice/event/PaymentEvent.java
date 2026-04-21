package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Apstraktna bazna klasa za sve payment-related evente.
 * Sadrži zajedničke podatke za plaćanje.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class PaymentEvent extends InvoiceEvent {
    protected boolean afterExpiration;
    protected String paymentMethodId;
    protected PaymentDetail payment;

    protected PaymentEvent(InvoiceEventType type) {
        super(type);
    }

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for PaymentEvent");
        }
        if (paymentMethodId == null || paymentMethodId.isBlank()) {
            throw new IllegalArgumentException("Payment Method ID is required for PaymentEvent");
        }
        if (payment != null) {
            payment.validate();
        }
    }
}

