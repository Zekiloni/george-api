package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.tracking.TrackingEvent;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.tracking.TrackingEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingEventEntityMapper {

    @Mapping(source = "trackingLink.id", target = "trackingLinkId")
    @Mapping(target = "trackingLink", ignore = true)
    TrackingEvent toDomain(TrackingEventEntity entity);

    @Mapping(target = "trackingLink", ignore = true)
    TrackingEventEntity toEntity(TrackingEvent domain);
}

