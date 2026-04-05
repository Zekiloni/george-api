package com.zekiloni.george.provisioning.domain.billing.model;

import com.zekiloni.george.common.domain.model.Money;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InvoiceItem {
    private String id;
    private String description;
    private int quantity;
    private Money unitPrice;
    private Money discountAmount;
    private Offering offering;

    public Money getSubtotalAmount() {
        return unitPrice.multiply(quantity);
    }

    public Money getTotalAmount() {
        return getSubtotalAmount().subtract(discountAmount);
    }
}
