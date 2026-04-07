package com.zekiloni.george.provisioning.infrastructure.input.web.order.dto.event;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class InvoiceProcessingDto extends BtcPayEventDto {
    private boolean overPaid;
}