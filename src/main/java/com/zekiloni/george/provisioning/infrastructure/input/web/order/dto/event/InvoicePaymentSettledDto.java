package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoicePaymentSettledDto extends BtcPayEventDto {
    private boolean afterExpiration;
    private String paymentMethodId;
    private PaymentDetailDto payment;
}