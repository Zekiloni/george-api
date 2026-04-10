package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.tracking.TrackingLink;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.tracking.TrackingLinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingLinkEntityMapper {
    TrackingLink toDomain(TrackingLinkEntity entity);
    TrackingLinkEntity toEntity(TrackingLink domain);
}

