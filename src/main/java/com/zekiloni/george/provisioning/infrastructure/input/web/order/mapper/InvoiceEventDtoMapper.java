package com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper;

import com.zekiloni.george.provisioning.domain.order.model.invoice.event.*;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.event.*;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassExhaustiveStrategy;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

import java.time.OffsetDateTime;

@Mapper(
        subclassExhaustiveStrategy = SubclassExhaustiveStrategy.RUNTIME_EXCEPTION
)
public interface InvoiceEventDtoMapper {
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
}
