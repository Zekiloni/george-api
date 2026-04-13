package com.zekiloni.george.workspace.domain.page.form.field.metadata;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhoneField implements FieldMetadata{
    private String defaultCountryCode; // e.g., "US", "GB", "IN"
    private String allowedCountryCodes; // Comma-separated list of allowed country codes
    private String formatPattern; // e.g., "(XXX) XXX-XXXX" for US
    private Boolean includeCountrySelector = false;
    private Boolean enableInternationalFormat = false;
    private Boolean showCountryFlag = false;
    private Boolean validateActualNumber = false; // Use external service like Twilio
}

