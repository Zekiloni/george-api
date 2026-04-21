package com.zekiloni.george.commerce.infrastructure.out.persistence.order.mapper;

import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceEntity;
import org.mapstruct.Mapper;

@Mapper
public interface InvoiceEntityMapper {
    Invoice toDomain(InvoiceEntity entity);

    InvoiceEntity toEntity(Invoice domain);
}
