package com.zekiloni.george.platform.infrastructure.out.persistence.template.mapper;

import com.zekiloni.george.platform.domain.model.template.Category;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.entity.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper
public interface CategoryEntityMapper {

    /** Children are populated by the service tree-assembly step, never by the mapper. */
    @Mapping(target = "children", ignore = true)
    Category toDomain(CategoryEntity entity);

    List<Category> toDomain(List<CategoryEntity> entities);

    CategoryEntity toEntity(Category domain);
}
