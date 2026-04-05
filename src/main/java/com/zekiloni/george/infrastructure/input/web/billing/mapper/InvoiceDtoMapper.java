package com.zekiloni.george.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.domain.billing.model.Invoice;
import com.zekiloni.george.domain.billing.model.InvoiceItem;
import com.zekiloni.george.domain.common.model.Money;
import com.zekiloni.george.infrastructure.input.web.billing.dto.*;
import com.zekiloni.george.infrastructure.input.web.comon.dto.MoneyDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {OfferingDtoMapper.class})
public interface InvoiceDtoMapper {
    InvoiceDto toDto(Invoice invoice);

    @Mapping(target = "subtotalAmount", expression = "java(map(invoiceItem))")
    @Mapping(target = "totalAmount", expression = "java(mapTotal(invoiceItem))")
    InvoiceItemDto toDto(InvoiceItem invoiceItem);

    default MoneyDto toMoneyDto(Money money) {
        if (money == null) return null;
        return new MoneyDto(money.getCurrency(), money.getAmount());
    }

    default MoneyDto map(InvoiceItem item) {
        return item.getSubtotalAmount() != null ?
            new MoneyDto(item.getSubtotalAmount().getCurrency(), item.getSubtotalAmount().getAmount()) : null;
    }

    default MoneyDto mapTotal(InvoiceItem item) {
        return item.getTotalAmount() != null ?
            new MoneyDto(item.getTotalAmount().getCurrency(), item.getTotalAmount().getAmount()) : null;
    }
}

