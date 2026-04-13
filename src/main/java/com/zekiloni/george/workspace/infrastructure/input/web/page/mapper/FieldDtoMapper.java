package com.zekiloni.george.workspace.infrastructure.input.web.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.field.FieldOption;
import com.zekiloni.george.workspace.domain.page.form.field.FieldValidator;
import com.zekiloni.george.workspace.domain.page.form.field.FormField;
import com.zekiloni.george.workspace.domain.page.form.field.metadata.*;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.*;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.metadata.*;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;


@Mapper()
public interface FieldDtoMapper {
    FormField toDomain(FormFieldCreateDto fieldCreate);

    FormFieldDto toDto(FormField field);

    FieldOption toDomain(FieldOptionCreateDto optionCreate);
    FieldOptionDto toDto(FieldOption option);

    FieldValidator toDomain(FieldValidatorCreateDto validatorCreate);
    FieldValidatorDto toDto(FieldValidator validator);

    @SubclassMapping(source = CreditCardField.class, target = CreditCardFieldDto.class)
    @SubclassMapping(source = PasswordField.class,   target = PasswordFieldDto.class)
    @SubclassMapping(source = AddressField.class,    target = AddressFieldDto.class)
    @SubclassMapping(source = PhoneField.class,      target = PhoneFieldDto.class)
    @SubclassMapping(source = RatingField.class,     target = RatingFieldDto.class)
    FieldMetadataDto toDto(FieldMetadata metadata);

    @SubclassMapping(source = CreditCardFieldDto.class, target = CreditCardField.class)
    @SubclassMapping(source = PasswordFieldDto.class,   target = PasswordField.class)
    @SubclassMapping(source = AddressFieldDto.class,    target = AddressField.class)
    @SubclassMapping(source = PhoneFieldDto.class,      target = PhoneField.class)
    @SubclassMapping(source = RatingFieldDto.class,     target = RatingField.class)
    FieldMetadata toDomain(FieldMetadataDto metadata);

    @ObjectFactory
    default FieldMetadata createDomain(FieldMetadataDto metadata) {
        if (metadata == null) return null;
        return switch (metadata) {
            case CreditCardFieldDto m -> toDomain(m);
            case PasswordFieldDto m   -> toDomain(m);
            case AddressFieldDto m    -> toDomain(m);
            case PhoneFieldDto m      -> toDomain(m);
            case RatingFieldDto m     -> toDomain(m);
            default -> throw new IllegalArgumentException("Unknown metadata type: " + metadata.getClass());
        };
    }

    @ObjectFactory
    default FieldMetadataDto createDto(FieldMetadata metadata) {
        if (metadata == null) return null;
        return switch (metadata) {
            case CreditCardField m -> toDto(m);
            case PasswordField m   -> toDto(m);
            case AddressField m    -> toDto(m);
            case PhoneField m      -> toDto(m);
            case RatingField m     -> toDto(m);
            default -> throw new IllegalArgumentException("Unknown metadata type: " + metadata.getClass());
        };
    }
}
