package com.zekiloni.george.commerce.application.port.out;

import java.time.OffsetDateTime;
import java.util.List;

public interface ExternalInvoicePort {
    ExternalInvoice createInvoice(String orderId, String description, String amount, String currency);

    /** Fetch live payment methods + status from the external provider. */
    ExternalInvoiceStatus getInvoiceStatus(String invoiceId);

    record ExternalInvoice(String invoiceId, String paymentUrl) { }

    /**
     * Snapshot of the external invoice's settlement state — what status it's in,
     * when it expires, and one entry per offered payment method (BTC, LBTC, LN, etc.)
     * with the address, BIP-21 link, amount due, exchange rate, and a list of payments
     * already received.
     */
    record ExternalInvoiceStatus(
            String invoiceId,
            String status,
            OffsetDateTime expiresAt,
            List<PaymentMethod> paymentMethods
    ) { }

    record PaymentMethod(
            /** e.g. "BTC", "BTC-LightningNetwork" */
            String paymentMethod,
            /** Bitcoin address (or LN BOLT11) */
            String destination,
            /** BIP-21 URI: bitcoin:address?amount=X — drop into a QR or wallet deep link */
            String paymentLink,
            /** Exchange rate locked at invoice creation time */
            String rate,
            /** Amount still owed in the crypto unit */
            String due,
            /** Total invoice amount in the crypto unit */
            String amount,
            /** Sum of payments received in the crypto unit */
            String totalPaid
    ) { }
}
