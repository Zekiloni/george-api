package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create DTO for CreditCardField.
 * Extends FormFieldCreateDto to inherit all base field properties (fieldName, label, etc.).
 * Does not include id, createdAt, updatedAt fields.
 * Adds specialized credit card field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CreditCardFieldCreateDto extends FormFieldCreateDto {
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

