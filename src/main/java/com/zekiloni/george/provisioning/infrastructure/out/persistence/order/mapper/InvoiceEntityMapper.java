package com.zekiloni.george.provisioning.infrastructure.out.persistence.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.invoice.Invoice;
import com.zekiloni.george.provisioning.infrastructure.out.persistence.order.entity.InvoiceEntity;
import org.mapstruct.Mapper;

@Mapper
public interface InvoiceEntityMapper {
    Invoice toDomain(InvoiceEntity entity);

    InvoiceEntity toEntity(Invoice domain);
}
