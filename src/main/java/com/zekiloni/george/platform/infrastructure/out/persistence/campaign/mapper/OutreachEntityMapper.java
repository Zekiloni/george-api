package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.OutreachEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Stream;

@Mapper
public interface OutreachEntityMapper {
    OutreachEntity toEntity(Outreach outreach);

    Outreach toDomain(OutreachEntity outreach);

    List<Outreach> toDomain(List<OutreachEntity> outreachEntities);

    List<OutreachEntity> toEntity(List<Outreach> outreach);

    Stream<Outreach> toDomain(Stream<OutreachEntity> outreachEntities);
}
