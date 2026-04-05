package com.zekiloni.george.platform.infrastructure.out.persistence.lead.mapper;

import com.zekiloni.george.platform.domain.model.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper
public interface LeadEntityMapper {
    LeadEntity toEntity(Lead lead);
    List<LeadEntity> toEntity(List<Lead> leads);
    Lead toDomain(LeadEntity lead);
    List<Lead> toDomain(List<LeadEntity> leadEntities);
}
