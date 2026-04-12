package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View DTO for PasswordField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized password field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PasswordFieldDto extends FormFieldDto {
    private Boolean requireUppercase;
    private Boolean requireLowercase;
    private Boolean requireNumbers;
    private Boolean requireSpecialChars;
    private Integer minStrength;
    private Boolean showStrengthIndicator;
    private Boolean allowShowPassword;
}

