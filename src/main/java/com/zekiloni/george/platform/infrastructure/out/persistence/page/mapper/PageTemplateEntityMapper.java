package com.zekiloni.george.platform.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.PageTemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface PageTemplateEntityMapper {
    PageTemplateEntity toEntity(PageTemplate template);
    PageTemplate toDomain(PageTemplateEntity entity);
    void updateEntityFromDomain(PageTemplate domain, @MappingTarget PageTemplateEntity entity);
}
