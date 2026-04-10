package com.zekiloni.george.workspace.domain.page.form.field;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized FormField for phone number input.
 * Supports various phone formats and country-specific requirements.
 */
@Entity
@Table(name = "phone_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("PHONE_NUMBER")
public class PhoneField extends FormField {

    @Column(name = "default_country_code")
    private String defaultCountryCode; // e.g., "US", "GB", "IN"

    @Column(name = "allowed_country_codes")
    private String allowedCountryCodes; // Comma-separated list of allowed country codes

    @Column(name = "format_pattern")
    private String formatPattern; // e.g., "(XXX) XXX-XXXX" for US

    @Column(name = "include_country_selector")
    private Boolean includeCountrySelector = false;

    @Column(name = "enable_international_format")
    private Boolean enableInternationalFormat = false;

    @Column(name = "show_country_flag")
    private Boolean showCountryFlag = false;

    @Column(name = "validate_actual_number")
    private Boolean validateActualNumber = false; // Use external service like Twilio
}

