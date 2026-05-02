package com.zekiloni.george.commerce.domain.order.model.invoice;

/**
 * Whether an invoice represents an initial purchase or a periodic renewal.
 * Drives:
 *  - which {@link com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic.PriceImpact}
 *    characteristic adjustments are included (ONE_TIME applied only on NEW_PURCHASE)
 *  - the payment-handler's behavior on PAID (NEW_PURCHASE provisions, RENEWAL extends validTo)
 */
public enum InvoiceType {
    NEW_PURCHASE,
    RENEWAL
}
