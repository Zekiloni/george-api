package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PasswordField extends FormField {
    private Boolean requireUppercase = false;
    private Boolean requireLowercase = false;
    private Boolean requireNumbers = false;
    private Boolean requireSpecialChars = false;
    private Integer minStrength = 0; // 0-4: weak, fair, good, strong, very strong
    private Boolean showStrengthIndicator = true;
    private Boolean allowShowPassword = true;
}

