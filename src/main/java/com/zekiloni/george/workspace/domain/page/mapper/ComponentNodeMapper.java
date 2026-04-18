package com.zekiloni.george.workspace.domain.page.mapper;

import com.zekiloni.george.workspace.domain.page.definition.ComponentNode;
import com.zekiloni.george.workspace.domain.page.dto.ComponentNodeDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * MapStruct mapper za ComponentNode.
 * Mapira između ComponentNode domenskog modela i DTO klasa.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ComponentNodeMapper {

    /**
     * Mapira ComponentNode na ComponentNodeDto.
     */
    ComponentNodeDto toDto(ComponentNode entity);

    /**
     * Mapira ComponentNodeDto na ComponentNode.
     */
    ComponentNode toEntity(ComponentNodeDto dto);
}

