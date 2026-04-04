package com.zekiloni.george.infrastructure.input.web.billing.mapper;

import com.zekiloni.george.domain.billing.model.Invoice;
import com.zekiloni.george.domain.billing.model.InvoiceItem;
import com.zekiloni.george.infrastructure.input.web.billing.dto.*;
import org.mapstruct.Mapper;

@Mapper
public interface InvoiceDtoMapper {
    InvoiceDto toDto(Invoice invoice);

    InvoiceItemDto toDto(InvoiceItem invoiceItem);
}

