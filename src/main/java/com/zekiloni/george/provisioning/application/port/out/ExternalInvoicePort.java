package com.zekiloni.george.provisioning.application.port.out;

public interface ExternalInvoicePort {
    String createInvoice(String orderId, String description, String amount, String currency);
}
