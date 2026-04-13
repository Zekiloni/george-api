package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * View DTO for AddressField.
 * Extends FormFieldDto to inherit all base field properties (id, createdAt, updatedAt, etc.).
 * Adds specialized address field properties.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressFieldDto implements FieldMetadataDto {
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

