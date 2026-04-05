package com.zekiloni.george.workspace.domain.campaign.form.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Specialized FormField for address input.
 * Supports both single-line and multi-part address formats.
 */
@Entity
@Table(name = "address_fields")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@DiscriminatorValue("ADDRESS")
public class AddressField extends FormField {

    @Column(name = "include_street")
    private Boolean includeStreet = true;

    @Column(name = "include_city")
    private Boolean includeCity = true;

    @Column(name = "include_state")
    private Boolean includeState = true;

    @Column(name = "include_zip")
    private Boolean includeZip = true;

    @Column(name = "include_country")
    private Boolean includeCountry = true;

    @Column(name = "include_apartment")
    private Boolean includeApartment = false;

    @Column(name = "format_multipart")
    private Boolean formatMultipart = true; // If true, shows separate fields; if false, single textarea

    @Column(name = "require_country_code")
    private String requireCountryCode; // Optional: restrict to specific country code

    @Column(name = "enable_autocomplete")
    private Boolean enableAutocomplete = false;
}

