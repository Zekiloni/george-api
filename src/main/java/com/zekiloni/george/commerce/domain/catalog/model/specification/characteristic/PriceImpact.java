package com.zekiloni.george.commerce.domain.catalog.model.specification.characteristic;

/**
 * When a characteristic's chosen value adjusts the order total.
 *
 * <ul>
 *   <li>{@code NONE} — characteristic doesn't affect price (server region tag, OS choice).</li>
 *   <li>{@code ONE_TIME} — applied on first purchase only (setup fee, one-shot port allocation).
 *       Skipped on RENEWAL invoices.</li>
 *   <li>{@code RECURRING} — applied every billing cycle (RAM upgrade, dedicated-port surcharge,
 *       priority upgrade). Charged on both NEW_PURCHASE and RENEWAL invoices.</li>
 * </ul>
 */
public enum PriceImpact {
    NONE,
    ONE_TIME,
    RECURRING
}
