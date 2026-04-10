package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.field.FieldOption;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.form.field.FieldOptionEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface FieldOptionEntityMapper {

    FieldOptionEntity toEntity(FieldOption domain);

    FieldOption toDomain(FieldOptionEntity entity);
}

