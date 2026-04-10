package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("PASSWORD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PasswordFieldEntity extends FormFieldEntity {

    @Column(name = "require_uppercase")
    private Boolean requireUppercase = false;

    @Column(name = "require_lowercase")
    private Boolean requireLowercase = false;

    @Column(name = "require_numbers")
    private Boolean requireNumbers = false;

    @Column(name = "require_special_chars")
    private Boolean requireSpecialChars = false;

    @Column(name = "min_strength")
    private Integer minStrength = 0;

    @Column(name = "show_strength_indicator")
    private Boolean showStrengthIndicator = true;

    @Column(name = "allow_show_password")
    private Boolean allowShowPassword = true;
}

