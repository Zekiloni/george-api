package com.zekiloni.george.workspace.domain.page.form.field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddressField extends FormField {
    private Boolean includeStreet = true;
    private Boolean includeCity = true;
    private Boolean includeState = true;
    private Boolean includeZip = true;
    private Boolean includeCountry = true;
    private Boolean includeApartment = false;
    private Boolean formatMultipart = true;
    private String requireCountryCode;
    private Boolean enableAutocomplete = false;
}

