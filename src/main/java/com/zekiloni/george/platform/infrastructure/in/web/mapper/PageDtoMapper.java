package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.page.PageUpdateDto;
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
