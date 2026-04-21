package com.zekiloni.george.commerce.domain.order.model.invoice;

import com.zekiloni.george.common.domain.model.Money;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class InvoiceItem {
    private String id;
    private Offering offering;
    private int quantity;
    private Money unitPrice;
    private BigDecimal discountAmount;

    public Money getSubtotal() {
        return unitPrice.multiply(quantity);
    }

    public Money getTotal() {
        if (discountAmount == null) return getSubtotal();
        return getSubtotal().subtract(new Money(getSubtotal().getCurrency(), discountAmount));
    }
}
