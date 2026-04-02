package com.zekiloni.george.domain.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specialized PasswordField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordFieldDTO {

    private Boolean requireUppercase;
    private Boolean requireLowercase;
    private Boolean requireNumbers;
    private Boolean requireSpecialChars;
    private Integer minStrength;
    private Boolean showStrengthIndicator;
    private Boolean allowShowPassword;
}

