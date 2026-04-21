package com.zekiloni.george.commerce.infrastructure.in.web.order.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceInvalidDto extends BtcPayEventDto {
    private boolean manuallyMarked;
}