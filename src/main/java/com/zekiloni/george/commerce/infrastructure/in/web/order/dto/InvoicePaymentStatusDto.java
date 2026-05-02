package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record InvoicePaymentStatusDto(
        String invoiceId,
        String status,
        OffsetDateTime expiresAt,
        List<PaymentMethodDto> paymentMethods
) {
    public record PaymentMethodDto(
            String paymentMethod,
            String destination,
            String paymentLink,
            String rate,
            String due,
            String amount,
            String totalPaid
    ) { }
}
