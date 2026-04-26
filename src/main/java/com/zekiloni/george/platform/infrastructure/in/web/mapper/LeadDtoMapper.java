package com.zekiloni.george.platform.infrastructure.in.web.mapper;

import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.platform.infrastructure.in.web.dto.LeadDto;
import org.mapstruct.Mapper;

@Mapper
public interface LeadDtoMapper {
    LeadDto toDto(Lead lead);
}
