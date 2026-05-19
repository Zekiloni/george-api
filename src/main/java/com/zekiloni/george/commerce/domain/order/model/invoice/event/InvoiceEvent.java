package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import com.zekiloni.george.common.domain.model.DomainEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@SuperBuilder
@NoArgsConstructor
public abstract class InvoiceEvent implements DomainEvent {
    public static final String ORDER_ID = "orderId";

    protected String deliveryId;
    protected String webhookId;
    protected String originalDeliveryId;
    protected boolean isRedelivery;
    protected InvoiceEventType type;
    protected OffsetDateTime timestamp;
    protected String storeId;
    protected String invoiceId;
    protected Map<String, Object> metadata;

    protected InvoiceEvent(InvoiceEventType type) {
        this.type = type;
    }

    public String getOrderId() {
        return metadata != null ? (String) metadata.get(ORDER_ID) : null;
    }

    public abstract void validate();
}
