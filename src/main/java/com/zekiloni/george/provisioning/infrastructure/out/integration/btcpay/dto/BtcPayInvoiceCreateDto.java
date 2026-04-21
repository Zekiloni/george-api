package com.zekiloni.george.provisioning.infrastructure.out.integration.btcpay.dto;

import java.util.List;
import java.util.Map;

public record BtcPayInvoiceCreateDto(
        Metadata metadata,
        Checkout checkout,
        Receipt receipt,
        String amount,
        String currency,
        List<String> additionalSearchTerms
) {
    public record Metadata(
            String orderId,
            String orderUrl,
            String itemDesc,
            Map<String, Object> posData,
            Map<String, Object> receiptData
    ) {}

    public record Checkout(
            String speedPolicy,           // HighSpeed, MediumSpeed, LowSpeed, LowMediumSpeed
            List<String> paymentMethods,
            String defaultPaymentMethod,  // BTC-CHAIN, BTC-LN, itd.
            boolean lazyPaymentMethods,
            int expirationMinutes,
            int monitoringMinutes,
            double paymentTolerance,
            String redirectURL,
            boolean redirectAutomatically,
            String defaultLanguage
    ) {}

    public record Receipt(
            boolean enabled,
            Boolean showQR,        // nullable
            Boolean showPayments   // nullable
    ) {}
}