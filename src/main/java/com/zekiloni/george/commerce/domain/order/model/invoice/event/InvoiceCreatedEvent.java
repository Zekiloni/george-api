package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class InvoiceCreatedEvent extends InvoiceEvent {
    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceCreatedEvent");
        }

        if (getOrderId() == null || getOrderId().isBlank()) {
            throw new IllegalArgumentException("Order ID is required for InvoiceCreatedEvent");
        }
    }
}

