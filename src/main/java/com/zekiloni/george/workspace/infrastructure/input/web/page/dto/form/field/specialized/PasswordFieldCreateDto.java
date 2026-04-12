package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create DTO for PasswordField.
 * Extends FormFieldCreateDto to inherit all base field properties (fieldName, label, etc.).
 * Does not include id, createdAt, updatedAt fields.
 * Adds specialized password field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PasswordFieldCreateDto extends FormFieldCreateDto {
    private Boolean requireUppercase;
    private Boolean requireLowercase;
    private Boolean requireNumbers;
    private Boolean requireSpecialChars;
    private Integer minStrength;
    private Boolean showStrengthIndicator;
    private Boolean allowShowPassword;
}

