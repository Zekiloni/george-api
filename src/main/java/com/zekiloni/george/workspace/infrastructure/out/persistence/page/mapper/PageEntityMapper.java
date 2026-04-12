package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.PageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {FormConfigEntityMapper.class})
public interface PageEntityMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "faviconUrl", target = "faviconUrl")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "form", target = "form")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    PageEntity toEntity(Page page);

    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "faviconUrl", target = "faviconUrl")
    @Mapping(source = "status", target = "status")
    @Mapping(source = "formConfig", target = "form")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "updatedAt", target = "updatedAt")
    Page toDomain(PageEntity page);
}
