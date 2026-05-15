package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageTemplateDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PageTemplateDtoMapper {
    PageTemplateDto toDto(PageTemplate template);
    PageTemplate toDomain(PageTemplateDto dto);
}
