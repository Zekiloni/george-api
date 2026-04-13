package com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreditCardFieldDto.class,  name = "CREDIT_CARD"),
        @JsonSubTypes.Type(value = PasswordFieldDto.class,    name = "PASSWORD"),
        @JsonSubTypes.Type(value = AddressFieldDto.class,     name = "ADDRESS"),
        @JsonSubTypes.Type(value = PhoneFieldDto.class,       name = "PHONE_NUMBER"),
        @JsonSubTypes.Type(value = RatingFieldDto.class,      name = "RATING")
})
public interface FieldMetadataDto {
}
