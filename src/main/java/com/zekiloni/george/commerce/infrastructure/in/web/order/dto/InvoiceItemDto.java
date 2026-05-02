package com.zekiloni.george.commerce.infrastructure.in.web.order.dto;

import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
import com.zekiloni.george.commerce.infrastructure.in.web.catalog.dto.OfferingDto;

public record InvoiceItemDto(
        String id,
        int units,
        MoneyDto totalAmount,
        OfferingDto offering
) {
}
