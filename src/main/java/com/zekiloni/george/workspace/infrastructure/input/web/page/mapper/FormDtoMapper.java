package com.zekiloni.george.workspace.infrastructure.input.web.page.mapper;

import com.zekiloni.george.workspace.domain.page.form.Form;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.FormCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.form.FormDto;
import org.mapstruct.Mapper;

@Mapper(uses = {FieldDtoMapper.class})
public interface FormDtoMapper {
    Form toDomain(FormCreateDto form);

    FormDto toDto(Form form);
}
