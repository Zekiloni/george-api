package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.field.FieldValidator;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FieldValidatorEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FieldValidatorEntityMapper {

    FieldValidatorEntity toEntity(FieldValidator domain);

    FieldValidator toDomain(FieldValidatorEntity entity);
}

