package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.field.FieldOption;
import com.zekiloni.george.workspace.domain.page.form.field.FieldValidator;
import com.zekiloni.george.workspace.domain.page.form.field.FormField;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FieldOptionEntity;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FieldValidatorEntity;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FormFieldEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface FormFieldEntityMapper {
    FormFieldEntity toEntity(FormField domain);
    FormField toDomain(FormFieldEntity entity);

    FieldValidatorEntity toEntity(FieldValidator domain);

    FieldValidator toDomain(FieldValidatorEntity entity);

    FieldOptionEntity toEntity(FieldOption domain);

    FieldOption toDomain(FieldOptionEntity entity);
}

