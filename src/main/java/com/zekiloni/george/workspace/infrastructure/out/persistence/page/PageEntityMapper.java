package com.zekiloni.george.workspace.infrastructure.out.persistence.page;

import com.zekiloni.george.workspace.domain.page.Page;
import org.mapstruct.Mapper;

@Mapper
public interface PageEntityMapper {
    PageEntity toEntity(Page page);

    Page toDomain(PageEntity page);
}
