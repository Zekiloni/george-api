package com.zekiloni.george.provisioning.domain.billing.model;

import com.zekiloni.george.common.domain.model.Money;
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
    private Money subtotal;
    private Money discountAmount;
    private Money taxAmount;
    private String paymentMethod;
    private String paymentReference;
    private OffsetDateTime issuedAt;
    private OffsetDateTime dueAt;
    private OffsetDateTime paidAt;
    private OffsetDateTime cancelledAt;
    private OffsetDateTime refundedAt;

    private Money getTotalAmount() {
        return subtotal.subtract(discountAmount).add(taxAmount);
    }

    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
