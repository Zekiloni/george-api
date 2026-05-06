package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageTemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PageTemplateDtoMapper {
    PageTemplateDto toDto(PageTemplate template);
    PageTemplate toDomain(PageTemplateDto dto);
}
