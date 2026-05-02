package com.zekiloni.george.commerce.domain.order.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Represents customer intent only — what offering, how many units, what configuration.
 * Pricing is NOT snapshotted here; the only price snapshot is on
 * {@link com.zekiloni.george.commerce.domain.order.model.invoice.InvoiceItem#getTotalAmount()}.
 */
@Data
@Builder
public class OrderItem {
    private String id;
    private Offering offering;
    private Integer units;
    private List<Characteristic> characteristic;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
