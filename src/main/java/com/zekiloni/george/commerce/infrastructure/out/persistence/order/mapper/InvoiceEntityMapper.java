package com.zekiloni.george.commerce.infrastructure.out.persistence.order.mapper;

import com.zekiloni.george.commerce.domain.order.model.invoice.Invoice;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.entity.InvoiceEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface InvoiceEntityMapper {
    Invoice toDomain(InvoiceEntity entity);

    InvoiceEntity toEntity(Invoice domain);

    @AfterMapping
    default void linkItems(@MappingTarget InvoiceEntity entity) {
        if (entity.getItems() != null) {
            entity.getItems().forEach(item -> item.setInvoice(entity));
        }
    }
}
