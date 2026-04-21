package com.zekiloni.george.commerce.application.port.out;

public interface ExternalInvoicePort {
    ExternalInvoice createInvoice(String orderId, String description, String amount, String currency);

    record ExternalInvoice(String invoiceId, String paymentUrl) { }
}
