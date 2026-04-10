package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.tracking.TrackingEvent;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.tracking.TrackingEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingEventEntityMapper {
    TrackingEvent toDomain(TrackingEventEntity entity);
    TrackingEventEntity toEntity(TrackingEvent domain);
}

