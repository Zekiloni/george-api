package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * View DTO for AddressField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized address field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AddressFieldDto extends FormFieldDto {
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

