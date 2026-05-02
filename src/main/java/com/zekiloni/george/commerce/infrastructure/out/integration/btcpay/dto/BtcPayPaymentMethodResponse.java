package com.zekiloni.george.commerce.infrastructure.out.integration.btcpay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * One entry returned by {@code GET /api/v1/stores/{storeId}/invoices/{invoiceId}/payment-methods}.
 * BTCPay returns more fields than we use; ignore unknowns to stay forward-compat.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record BtcPayPaymentMethodResponse(
        String paymentMethod,
        String destination,
        String paymentLink,
        String rate,
        String paymentMethodPaid,
        String totalPaid,
        String due,
        String amount,
        String networkFee,
        Boolean activated
) { }
