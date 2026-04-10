package com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@DiscriminatorValue("ADDRESS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class AddressFieldEntity extends FormFieldEntity {

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
    private Boolean formatMultipart = true;

    @Column(name = "require_country_code")
    private String requireCountryCode;

    @Column(name = "enable_autocomplete")
    private Boolean enableAutocomplete = false;
}

