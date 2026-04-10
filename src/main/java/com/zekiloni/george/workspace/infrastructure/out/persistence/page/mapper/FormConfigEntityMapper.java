package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.FormConfig;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.FormConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FormFieldEntityMapper.class})
public interface FormConfigEntityMapper {
    FormConfigEntity toEntity(FormConfig domain);
    FormConfig toDomain(FormConfigEntity entity);
}

