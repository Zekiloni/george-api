package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View DTO for PasswordField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized password field properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordFieldDto implements FieldMetadataDto {
    private Boolean requireUppercase;
    private Boolean requireLowercase;
    private Boolean requireNumbers;
    private Boolean requireSpecialChars;
    private Integer minStrength;
    private Boolean showStrengthIndicator;
    private Boolean allowShowPassword;
}

