package com.zekiloni.george.provisioning.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.provisioning.domain.billing.model.Invoice;
import com.zekiloni.george.provisioning.domain.billing.model.InvoiceItem;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.InvoiceDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.billing.dto.InvoiceItemDto;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
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

