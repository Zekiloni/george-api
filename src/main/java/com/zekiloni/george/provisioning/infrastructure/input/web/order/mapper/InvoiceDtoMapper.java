package com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;
import com.zekiloni.george.provisioning.domain.order.model.invoice.InvoiceItem;
import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.provisioning.domain.order.model.invoice.event.*;
import com.zekiloni.george.provisioning.infrastructure.input.web.catalog.mapper.OfferingDtoMapper;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.InvoiceDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.InvoiceItemDto;
import com.zekiloni.george.common.infrastructure.in.web.dto.MoneyDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.event.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

import java.time.OffsetDateTime;

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

    @SubclassMappings({
            @SubclassMapping(target = InvoiceCreatedEvent.class, source = InvoiceCreatedDto.class),
            @SubclassMapping(target = InvoiceExpiredEvent.class, source = InvoiceExpiredDto.class),
            @SubclassMapping(target = InvoiceInvalidEvent.class, source = InvoiceInvalidDto.class),
            @SubclassMapping(target = InvoiceReceivedPaymentEvent.class, source = InvoiceReceivedPaymentDto.class),
            @SubclassMapping(target = InvoicePaymentSettledEvent.class, source = InvoicePaymentSettledDto.class),
            @SubclassMapping(target = InvoiceSettledEvent.class, source = InvoiceSettledDto.class),
            @SubclassMapping(target = InvoiceProcessingEvent.class, source = InvoiceProcessingDto.class),
    })
    InvoiceEvent toDomain(BtcPayEventDto event);

    default OffsetDateTime toOffsetDateTime(int epochMilli) {
        return OffsetDateTime.ofInstant(java.time.Instant.ofEpochMilli(epochMilli), java.time.ZoneOffset.UTC);
    }

    default MoneyDto map(InvoiceItem item) {
        return item.getSubtotal() != null ?
            new MoneyDto(item.getSubtotal().getCurrency(), item.getSubtotal().getAmount()) : null;
    }

    default MoneyDto mapTotal(InvoiceItem item) {
        return item.getTotal() != null ?
            new MoneyDto(item.getTotal().getCurrency(), item.getTotal().getAmount()) : null;
    }
}

