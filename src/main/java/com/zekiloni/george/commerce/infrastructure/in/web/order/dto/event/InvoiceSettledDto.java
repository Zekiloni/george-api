package com.zekiloni.george.commerce.infrastructure.in.web.order.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceSettledDto extends BtcPayEventDto {
    private boolean manuallyMarked;
    private boolean overPaid;
}