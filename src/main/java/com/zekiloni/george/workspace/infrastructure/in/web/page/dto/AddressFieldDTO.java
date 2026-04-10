package com.zekiloni.george.workspace.infrastructure.in.web.page.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for specialized AddressField.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressFieldDTO {

    private Boolean includeStreet;
    private Boolean includeCity;
    private Boolean includeState;
    private Boolean includeZip;
    private Boolean includeCountry;
    private Boolean includeApartment;
    private Boolean formatMultipart;
    private String requireCountryCode;
    private Boolean enableAutocomplete;
}

