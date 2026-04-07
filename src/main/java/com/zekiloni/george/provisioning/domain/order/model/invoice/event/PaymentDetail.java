package com.zekiloni.george.provisioning.domain.order.model.invoice.event;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

/**
 * Vrednostni objekat koji sadrži detalje o plaćanju.
 */
@Data
@Builder
public class PaymentDetail {
    private String id;
    private long receivedDate; // Unix timestamp
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

