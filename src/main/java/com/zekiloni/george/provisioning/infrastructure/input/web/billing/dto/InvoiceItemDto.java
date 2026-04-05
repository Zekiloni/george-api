package com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;

public record InvoiceItemDto(
        String id,
        String description,
        int quantity,
        MoneyDto unitPrice,
        MoneyDto discountAmount,
        MoneyDto subtotalAmount,
        MoneyDto totalAmount,
        OfferingDto offering
) {
}

