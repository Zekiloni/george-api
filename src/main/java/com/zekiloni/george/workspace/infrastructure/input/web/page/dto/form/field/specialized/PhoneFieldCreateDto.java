package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create DTO for PhoneField.
 * Extends FormFieldCreateDto to inherit all base field properties (fieldName, label, etc.).
 * Does not include id, createdAt, updatedAt fields.
 * Adds specialized phone field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PhoneFieldCreateDto extends FormFieldCreateDto {
    private String defaultCountryCode;
    private String allowedCountryCodes;
    private String formatPattern;
    private Boolean includeCountrySelector;
    private Boolean enableInternationalFormat;
    private Boolean showCountryFlag;
    private Boolean validateActualNumber;
}

