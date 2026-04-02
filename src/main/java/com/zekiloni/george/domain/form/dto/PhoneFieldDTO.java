package com.zekiloni.george.domain.form.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specialized PhoneField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhoneFieldDTO {

    private String defaultCountryCode;
    private String allowedCountryCodes;
    private String formatPattern;
    private Boolean includeCountrySelector;
    private Boolean enableInternationalFormat;
    private Boolean showCountryFlag;
    private Boolean validateActualNumber;
}

