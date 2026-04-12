package com.zekiloni.george.workspace.infrastructure.input.web.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.field.FieldOption;
import com.zekiloni.george.workspace.domain.page.form.field.FieldValidator;
import com.zekiloni.george.workspace.domain.page.form.field.FormField;
import com.zekiloni.george.workspace.domain.page.form.field.PhoneField;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.*;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized.PhoneFieldCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.field.specialized.PhoneFieldDto;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;


@Mapper()
public interface FieldDtoMapper {
    @SubclassMappings({
            @SubclassMapping(source = PhoneFieldCreateDto.class, target = PhoneField.class),
    })
    FormField toDomain(FormFieldCreateDto fieldCreate);

    @SubclassMappings({
            @SubclassMapping(source = PhoneField.class, target = PhoneFieldDto.class),
    })
    FormFieldDto toDto(FormField field);


    FieldOption toDomain(FieldOptionCreateDto optionCreate);
    FieldOptionDto toDto(FieldOption option);

    FieldValidator toDomain(FieldValidatorCreateDto validatorCreate);
    FieldValidatorDto toDto(FieldValidator validator);
}
