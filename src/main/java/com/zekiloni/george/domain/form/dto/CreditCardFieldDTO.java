package com.zekiloni.george.domain.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specialized CreditCardField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditCardFieldDTO {

    private Boolean includeCardNumber;
    private Boolean includeCardHolder;
    private Boolean includeExpiry;
    private Boolean includeCvv;
    private Boolean includeBillingAddress;
    private Boolean formatMultipart;
    private String allowedCardTypes;
    private Boolean showCardPreview;
    private Boolean requireBillingAddress;
    private Boolean enable3dSecure;
    private Boolean tokenizeCardData;
}

