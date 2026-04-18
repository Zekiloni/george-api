package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.PageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper za mapiranje između PageEntity (JPA) i Page (domenski model).
 * Automatski mapira sve polja uključujući PageDefinition kao JSON.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface PageEntityMapper {

    /**
     * Mapira Page domenski model na PageEntity (JPA entitet).
     */
    PageEntity toEntity(Page page);

    /**
     * Mapira PageEntity (JPA entitet) na Page domenski model.
     */
    Page toDomain(PageEntity pageEntity);

    /**
     * Ažurira PageEntity sa vrednostima iz Page domenskog modela.
     */
    void updateEntityFromDomain(Page domain, @MappingTarget PageEntity entity);
}
