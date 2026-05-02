package com.zekiloni.george.commerce.application.port.in;

import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;

public interface InvoiceCreateUseCase {
    /** Initial-purchase invoice for a freshly placed order. */
    Invoice create(Order order);

    /**
     * Renewal invoice for an existing {@link ServiceAccess}. The invoice references the access;
     * on payment, the payment handler extends {@code validTo} and resumes the service.
     */
    Invoice createRenewal(ServiceAccess access);
}
