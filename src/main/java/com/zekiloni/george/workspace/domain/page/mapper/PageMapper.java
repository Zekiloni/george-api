package com.zekiloni.george.workspace.domain.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;

import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PageMapper {
    Page toDomain(PageCreateDto dto);
    PageDto toDto(Page entity);
}

