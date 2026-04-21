package com.zekiloni.george.commerce.infrastructure.in.web.order.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoicePaymentSettledDto extends BtcPayEventDto {
    private boolean afterExpiration;
    private String paymentMethodId;
    private PaymentDetailDto payment;
}