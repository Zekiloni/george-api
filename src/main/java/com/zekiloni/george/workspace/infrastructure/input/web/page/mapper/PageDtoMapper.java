package com.zekiloni.george.workspace.infrastructure.input.web.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageDto;
import org.mapstruct.Mapper;

@Mapper(uses = {FormDtoMapper.class})
public interface PageDtoMapper {
    Page toDomain(PageCreateDto pageCreate);
    PageDto toDto(Page page);
}
