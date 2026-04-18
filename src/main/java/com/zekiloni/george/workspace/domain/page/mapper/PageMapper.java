package com.zekiloni.george.workspace.domain.page.mapper;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.domain.page.dto.PageResponseDto;
import com.zekiloni.george.workspace.domain.page.dto.PageUpdateDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper za Page entitet.
 * Mapira između Page domenskog modela i DTO klasa.
 */
@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface PageMapper {

    /**
     * Mapira PageCreateDto na Page entitet.
     * Koristi se pri kreiranju nove stranice.
     */
    Page toEntity(PageCreateDto dto);

    /**
     * Mapira Page entitet na PageResponseDto.
     * Koristi se pri čitanju stranice iz baze.
     */
    PageResponseDto toResponseDto(Page entity);

    /**
     * Ažurira postojeći Page entitet sa vrednostima iz PageUpdateDto.
     * Koristi se pri ažuriranju stranice.
     */
    void updateEntityFromDto(PageUpdateDto dto, @MappingTarget Page entity);
}

