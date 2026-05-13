package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.template.Category;
import com.zekiloni.george.platform.domain.model.template.Template;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.CategoryDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.CategoryUpsertDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.TemplateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.TemplateUpsertDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TemplateDtoMapper {

    CategoryDto toDto(Category category);
    List<CategoryDto> toCategoryDtos(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toDomain(CategoryUpsertDto dto);

    @Mapping(target = "isPublic", expression = "java(template.isPublic())")
    @Mapping(target = "stepCount", expression = "java(template.getSteps() == null ? 0 : template.getSteps().size())")
    TemplateDto toDto(Template template);

    List<TemplateDto> toTemplateDtos(List<Template> templates);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "usageCount", ignore = true)
    @Mapping(target = "tenantId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    Template toDomain(TemplateUpsertDto dto);
}
