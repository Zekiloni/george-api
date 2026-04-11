package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized FormField for phone number input.
 * Supports various phone formats and country-specific requirements.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhoneField extends FormField {
    private String defaultCountryCode; // e.g., "US", "GB", "IN"
    private String allowedCountryCodes; // Comma-separated list of allowed country codes
    private String formatPattern; // e.g., "(XXX) XXX-XXXX" for US
    private Boolean includeCountrySelector = false;
    private Boolean enableInternationalFormat = false;
    private Boolean showCountryFlag = false;
    private Boolean validateActualNumber = false; // Use external service like Twilio
}

