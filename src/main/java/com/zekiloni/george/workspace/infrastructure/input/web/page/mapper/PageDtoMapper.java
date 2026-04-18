package com.zekiloni.george.workspace.infrastructure.input.web.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper za mapiranje između web layer DTO-a i domenskog Page modela.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PageDtoMapper {

    /**
     * Mapira PageCreateDto (web ulaz) na Page domenski model.
     */
    Page toDomain(PageCreateDto webDto);

    /**
     * Mapira Page domenski model na PageDto (web izlaz).
     */
    PageDto toDto(Page page);

    /**
     * Ažurira Page domenski model sa vrednostima iz PageUpdateDto.
     */
    void updateDomainFromDto(PageUpdateDto webDto, @MappingTarget Page page);
}
