package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View DTO for CreditCardField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized credit card field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreditCardFieldDto extends FormFieldDto {
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
}

