package com.zekiloni.george.platform.infrastructure.out.persistence.template.mapper;

import com.zekiloni.george.platform.domain.model.template.Template;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.entity.TemplateEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TemplateEntityMapper {

    Template toDomain(TemplateEntity entity);

    List<Template> toDomain(List<TemplateEntity> entities);

    TemplateEntity toEntity(Template domain);
}
