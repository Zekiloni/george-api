package com.zekiloni.george.workspace.domain.page.mapper;

import com.zekiloni.george.workspace.domain.page.definition.ComponentNode;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.ComponentNodeDto;
import org.mapstruct.Mapper;

@Mapper
public interface ComponentNodeMapper {

    ComponentNodeDto toDto(ComponentNode entity);
    ComponentNode toEntity(ComponentNodeDto dto);
}

