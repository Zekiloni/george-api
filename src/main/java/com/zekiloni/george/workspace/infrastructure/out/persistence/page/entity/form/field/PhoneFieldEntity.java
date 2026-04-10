package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("PHONE_NUMBER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class PhoneFieldEntity extends FormFieldEntity {

    @Column(name = "default_country_code")
    private String defaultCountryCode;

    @Column(name = "allowed_country_codes")
    private String allowedCountryCodes;

    @Column(name = "format_pattern")
    private String formatPattern;

    @Column(name = "include_country_selector")
    private Boolean includeCountrySelector = false;

    @Column(name = "enable_international_format")
    private Boolean enableInternationalFormat = false;

    @Column(name = "show_country_flag")
    private Boolean showCountryFlag = false;

    @Column(name = "validate_actual_number")
    private Boolean validateActualNumber = false;
}

