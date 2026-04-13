package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.Form;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(uses = {FormFieldEntityMapper.class})
public interface FormEntityMapper {
    FormEntity toEntity(Form domain);
    Form toDomain(FormEntity entity);


    @AfterMapping
    default void link(@MappingTarget FormEntity entity) {
        if (entity.getField() != null) {
            entity.getField().forEach(field -> field.setForm(entity));
        }
    }
}

