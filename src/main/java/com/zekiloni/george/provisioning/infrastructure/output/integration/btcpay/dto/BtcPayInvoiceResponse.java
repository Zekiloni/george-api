package com.zekiloni.george.provisioning.infrastructure.output.integration.btcpay.dto;

import java.util.List;
import java.util.Map;

public record BtcPayInvoiceResponse(
        Metadata metadata,
        Checkout checkout,
        Receipt receipt,
        String id,
        String storeId,
        String amount,
        String paidAmount,
        String currency,
        String type,                              // Standard, TopUp
        String checkoutLink,
        Long createdTime,
        Long expirationTime,
        Long monitoringExpiration,
        String status,                            // New, Processing, Expired, Invalid, Settled
        String additionalStatus,                  // PaidLate, PaidPartial, Marked, itd.
        List<String> availableStatusesForManualMarking,
        boolean archived
) {
    public record Metadata(
            String orderId,
            String orderUrl,
            String itemDesc,
            Map<String, Object> posData,
            Map<String, Object> receiptData
    ) {}

    public record Checkout(
            String speedPolicy,
            List<String> paymentMethods,
            String defaultPaymentMethod,
            Boolean lazyPaymentMethods,    // ← was boolean
            Integer expirationMinutes,     // ← was int
            Integer monitoringMinutes,     // ← was int
            Double paymentTolerance,       // ← was double
            String redirectURL,
            Boolean redirectAutomatically, // ← was boolean
            String defaultLanguage
    ) {}

    public record Receipt(
            Boolean enabled,    // ← was boolean
            Boolean showQR,
            Boolean showPayments
    ) {}
}