package com.zekiloni.george.commerce.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
@Builder
public class PaymentDetail {
    private String id;
    private long receivedDate;
    private String value;
    private String fee;
    private PaymentStatus status;
    private String destination;

    public void validate() {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Payment ID is required");
        }
        if (status == null) {
            throw new IllegalArgumentException("Payment Status is required");
        }
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Payment value is required");
        }
    }
}
