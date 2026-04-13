package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View DTO for CreditCardField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized credit card field properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditCardFieldDto implements FieldMetadataDto {
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

