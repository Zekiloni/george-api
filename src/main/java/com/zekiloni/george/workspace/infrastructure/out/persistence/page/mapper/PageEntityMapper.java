package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.PageEntity;
import org.mapstruct.Mapper;

@Mapper
public interface PageEntityMapper {
    PageEntity toEntity(Page page);

    Page toDomain(PageEntity page);
}
