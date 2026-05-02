package com.zekiloni.george.commerce.domain.catalog.model;

/**
 * How {@link DiscountTier}s on a {@link Price} are interpreted.
 *
 * <ul>
 *   <li>{@code NONE} — no tiers; {@code basePrice × units} regardless of count.</li>
 *   <li>{@code VOLUME} — the highest-matching tier's discount applies to ALL units
 *       (e.g., "≥12 units → 20% off everything").</li>
 *   <li>{@code GRADUATED} — each unit is priced according to which bracket it falls in
 *       (e.g., "first 10 at full, next 40 at 10% off"). Reserved; not implemented yet.</li>
 * </ul>
 */
public enum TierMode {
    NONE,
    VOLUME,
    GRADUATED
}
