package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import com.zekiloni.george.provisioning.domain.billing.model.InvoiceStatus;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;

import java.time.OffsetDateTime;
import java.util.List;

public record InvoiceDto(
        String id,
        String invoiceNumber,
        OrderDto order,
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

