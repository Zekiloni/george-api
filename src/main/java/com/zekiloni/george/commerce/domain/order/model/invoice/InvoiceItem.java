package com.zekiloni.george.commerce.domain.order.model.invoice;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
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
public class InvoiceItem {
    private String id;
    private Offering offering;
    private int units;
    private Money totalAmount;

    public Money getTotal() {
        return totalAmount;
    }
}
