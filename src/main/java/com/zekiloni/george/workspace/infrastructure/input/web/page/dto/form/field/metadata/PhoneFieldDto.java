package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View DTO for PhoneField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized phone field properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneFieldDto implements FieldMetadataDto {
    private String defaultCountryCode;
    private String allowedCountryCodes;
    private String formatPattern;
    private Boolean includeCountrySelector;
    private Boolean enableInternationalFormat;
    private Boolean showCountryFlag;
    private Boolean validateActualNumber;
}

