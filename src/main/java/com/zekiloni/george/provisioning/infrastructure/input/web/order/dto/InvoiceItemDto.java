package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.dto.OfferingDto;

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

