package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View DTO for PhoneField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized phone field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PhoneFieldDto extends FormFieldDto {
    private String defaultCountryCode;
    private String allowedCountryCodes;
    private String formatPattern;
    private Boolean includeCountrySelector;
    private Boolean enableInternationalFormat;
    private Boolean showCountryFlag;
    private Boolean validateActualNumber;
}

