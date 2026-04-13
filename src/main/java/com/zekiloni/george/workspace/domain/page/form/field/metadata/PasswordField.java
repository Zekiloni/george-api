package com.zekiloni.george.workspace.domain.page.form.field.metadata;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordField implements FieldMetadata {
    private Boolean requireUppercase = false;
    private Boolean requireLowercase = false;
    private Boolean requireNumbers = false;
    private Boolean requireSpecialChars = false;
    private Integer minStrength = 0; // 0-4: weak, fair, good, strong, very strong
    private Boolean showStrengthIndicator = true;
    private Boolean allowShowPassword = true;
}

