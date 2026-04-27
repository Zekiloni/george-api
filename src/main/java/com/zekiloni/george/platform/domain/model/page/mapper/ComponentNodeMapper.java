package com.zekiloni.george.platform.domain.model.page.mapper;

import com.zekiloni.george.platform.domain.model.page.definition.ComponentNode;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.ComponentNodeDto;
import org.mapstruct.Mapper;

@Mapper
public interface ComponentNodeMapper {

    ComponentNodeDto toDto(ComponentNode entity);
    ComponentNode toEntity(ComponentNodeDto dto);
}

