package com.zekiloni.george.platform.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.platform.domain.session.TrackingLink;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.tracking.TrackingLinkEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TrackingLinkEntityMapper {
    TrackingLink toDomain(TrackingLinkEntity entity);
    TrackingLinkEntity toEntity(TrackingLink domain);
}

