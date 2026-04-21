package com.zekiloni.george.platform.domain.page.mapper;

import com.zekiloni.george.platform.domain.page.definition.ComponentNode;
import com.zekiloni.george.platform.infrastructure.in.web.dto.ComponentNodeDto;
import org.mapstruct.Mapper;

@Mapper
public interface ComponentNodeMapper {

    ComponentNodeDto toDto(ComponentNode entity);
    ComponentNode toEntity(ComponentNodeDto dto);
}

