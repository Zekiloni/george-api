package com.zekiloni.george.platform.domain.model;

import lombok.Builder;
import lombok.Getter;

/**
 * Result object containing resolved phone information
 */
@Getter
@Builder
public class PhoneResolutionResult {
    private final String phoneNumber;  // E.164 format (e.g. "+38733212345")
    private final String country;      // e.g. "BA", "US", "DE" (ISO country code)
    private final String areaCode;     // National destination code / area code (e.g. "32" for Sarajevo)
    private final String regionCode;   // Often same as areaCode or more specific
    private final String location;     // Human readable location (e.g. "Sarajevo", "New York")
}
