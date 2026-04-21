package com.zekiloni.george.provisioning.infrastructure.in.web.order.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
import com.zekiloni.george.provisioning.infrastructure.in.web.catalog.dto.OfferingDto;

import java.math.BigDecimal;

public record InvoiceItemDto(
        String id,
        String description,
        int quantity,
        MoneyDto unitPrice,
        BigDecimal discountAmount,
        MoneyDto subtotalAmount,
        MoneyDto totalAmount,
        OfferingDto offering
) {
}

