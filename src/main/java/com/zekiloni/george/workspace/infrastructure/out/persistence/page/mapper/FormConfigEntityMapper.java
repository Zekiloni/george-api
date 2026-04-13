package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.Form;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormEntity;
import org.mapstruct.Mapper;

@Mapper(uses = {FormFieldEntityMapper.class})
public interface FormConfigEntityMapper {
    FormEntity toEntity(Form domain);
    Form toDomain(FormEntity entity);
}

