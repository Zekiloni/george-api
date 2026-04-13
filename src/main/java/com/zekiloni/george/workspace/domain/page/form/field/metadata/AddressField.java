package com.zekiloni.george.workspace.domain.page.form.field.metadata;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressField implements FieldMetadata {
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

