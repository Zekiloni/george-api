package com.zekiloni.george.provisioning.infrastructure.in.web.order.dto.event;

import com.zekiloni.george.provisioning.domain.order.model.invoice.event.PaymentStatus;
import lombok.Data;

@Data
public class PaymentDetailDto {
    private String id;
    private int receivedDate;
    private String value;
    private String fee;
    private PaymentStatus status;
    private String destination;
}