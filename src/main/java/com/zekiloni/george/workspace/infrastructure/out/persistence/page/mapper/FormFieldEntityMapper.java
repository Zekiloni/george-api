package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.field.FormField;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FormFieldEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FieldValidatorEntityMapper.class, FieldOptionEntityMapper.class})
public interface FormFieldEntityMapper {
    FormFieldEntity toEntity(FormField domain);
    FormField toDomain(FormFieldEntity entity);
}

