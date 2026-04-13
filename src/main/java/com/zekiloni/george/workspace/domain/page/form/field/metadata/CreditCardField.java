package com.zekiloni.george.workspace.domain.page.form.field.metadata;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardField implements FieldMetadata {
    private Boolean includeCardNumber = true;
    private Boolean includeCardHolder = true;
    private Boolean includeExpiry = true;
    private Boolean includeCvv = true;
    private Boolean includeBillingAddress = false;
    private Boolean formatMultipart = true; // If true, shows separate fields; if false, single input
    private Boolean showCardPreview = true;
    private Boolean requireBillingAddress = false;
    private Boolean enable3dSecure = false;
    private Boolean tokenizeCardData = true; // Don't store raw card data
}

