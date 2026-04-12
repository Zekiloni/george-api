package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.FormFieldCreateDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Create DTO for AddressField.
 * Extends FormFieldCreateDto to inherit all base field properties (fieldName, label, etc.).
 * Does not include id, createdAt, updatedAt fields.
 * Adds specialized address field properties.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AddressFieldCreateDto extends FormFieldCreateDto {
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

