package com.zekiloni.george.commerce.application.port.in;

public interface RenewalInvoiceUseCase {
    /** Find ServiceAccess records nearing expiry and create RENEWAL invoices for them. */
    void handle();
}
