package com.zekiloni.george.commerce.infrastructure.in.web.order.dto.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.zekiloni.george.commerce.domain.order.model.invoice.event.InvoiceEventType;
import lombok.Data;

import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value = InvoiceCreatedDto.class, name = "InvoiceCreated"),
        @JsonSubTypes.Type(value = InvoiceExpiredDto.class, name = "InvoiceExpired"),
        @JsonSubTypes.Type(value = InvoiceReceivedPaymentDto.class, name = "InvoiceReceivedPayment"),
        @JsonSubTypes.Type(value = InvoicePaymentSettledDto.class, name = "InvoicePaymentSettled"),
        @JsonSubTypes.Type(value = InvoiceProcessingDto.class, name = "InvoiceProcessing"),
        @JsonSubTypes.Type(value = InvoiceInvalidDto.class, name = "InvoiceInvalid"),
        @JsonSubTypes.Type(value = InvoiceSettledDto.class, name = "InvoiceSettled"),
})
@Data
public class BtcPayEventDto {
    private String deliveryId;
    private String webhookId;
    private String originalDeliveryId;
    private boolean isRedelivery;
    private InvoiceEventType type;
    private int timestamp;
    private String storeId;
    private String invoiceId;
    private Map<String, Object> metadata;
}