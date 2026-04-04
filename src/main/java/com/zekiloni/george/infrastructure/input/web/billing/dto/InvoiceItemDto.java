package com.zekiloni.george.infrastructure.input.web.billing.dto;

import com.zekiloni.george.infrastructure.input.web.comon.dto.MoneyDto;

public record InvoiceItemDto(
        String id,
        String description,
        int quantity,
        MoneyDto unitPrice,
        MoneyDto discountAmount,
        MoneyDto subtotalAmount,
        MoneyDto totalAmount,
        PlanDto plan
) {
}

