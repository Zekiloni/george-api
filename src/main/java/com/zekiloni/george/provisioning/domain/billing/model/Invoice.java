package com.zekiloni.george.provisioning.domain.billing.model;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class Invoice {
    private String id;
    private String invoiceNumber;
    private Order order;
    private InvoiceStatus status;
    private List<InvoiceItem> items;
    private String paymentReference;
    private OffsetDateTime issuedAt;
    private OffsetDateTime dueAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime cancelledAt;
    private OffsetDateTime refundedAt;
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public Money getTotal() {
        return items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(Money.ZERO, Money::add);
    }
}
