package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

/**
 * Event koji se emituje kada je invoice istekao (nije plaćen u vremenu).
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class InvoiceExpiredEvent extends InvoiceEvent {
    private boolean partiallyPaid;

    @Override
    public void validate() {
        if (invoiceId == null || invoiceId.isBlank()) {
            throw new IllegalArgumentException("Invoice ID is required for InvoiceExpiredEvent");
        }
    }
}

