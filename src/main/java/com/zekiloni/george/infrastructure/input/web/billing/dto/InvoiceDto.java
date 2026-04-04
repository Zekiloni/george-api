package com.zekiloni.george.infrastructure.input.web.billing.dto;

import com.zekiloni.george.domain.billing.model.InvoiceStatus;
import com.zekiloni.george.infrastructure.input.web.comon.dto.MoneyDto;

import java.time.OffsetDateTime;
import java.util.List;

public record InvoiceDto(
        String id,
        String invoiceNumber,
        SubscriptionDto subscription,
        InvoiceStatus status,
        List<InvoiceItemDto> items,
        MoneyDto subtotal,
        MoneyDto discountAmount,
        MoneyDto taxAmount,
        MoneyDto totalAmount,
        String paymentMethod,
        String paymentReference,
        OffsetDateTime issuedAt,
        OffsetDateTime dueAt,
        OffsetDateTime paidAt,
        OffsetDateTime cancelledAt,
        OffsetDateTime refundedAt,
        String note,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

