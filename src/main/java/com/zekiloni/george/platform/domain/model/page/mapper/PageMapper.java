package com.zekiloni.george.platform.domain.model.page.mapper;

import com.zekiloni.george.platform.domain.model.page.Page;

import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageDto;
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

