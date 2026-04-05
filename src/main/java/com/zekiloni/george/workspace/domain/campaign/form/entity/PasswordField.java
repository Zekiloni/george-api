package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized FormField for password input.
 * Includes password-specific configurations like strength requirements.
 */
@Entity
@Table(name = "password_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PASSWORD")
public class PasswordField extends FormField {

    @Column(name = "require_uppercase")
    private Boolean requireUppercase = false;

    @Column(name = "require_lowercase")
    private Boolean requireLowercase = false;

    @Column(name = "require_numbers")
    private Boolean requireNumbers = false;

    @Column(name = "require_special_chars")
    private Boolean requireSpecialChars = false;

    @Column(name = "min_strength")
    private Integer minStrength = 0; // 0-4: weak, fair, good, strong, very strong

    @Column(name = "show_strength_indicator")
    private Boolean showStrengthIndicator = true;

    @Column(name = "allow_show_password")
    private Boolean allowShowPassword = true;
}

