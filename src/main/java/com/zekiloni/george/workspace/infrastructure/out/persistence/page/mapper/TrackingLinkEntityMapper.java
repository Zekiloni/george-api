package com.zekiloni.george.workspace.infrastructure.out.persistence.page.mapper;

import com.zekiloni.george.workspace.domain.page.tracking.TrackingLink;
import com.zekiloni.george.workspace.infrastructure.out.persistence.page.entity.tracking.TrackingLinkEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrackingLinkEntityMapper {

    @Mapping(source = "formSubmission.id", target = "formSubmissionId")
    @Mapping(target = "formSubmission", ignore = true)
    TrackingLink toDomain(TrackingLinkEntity entity);

    @Mapping(target = "formSubmission", ignore = true)
    TrackingLinkEntity toEntity(TrackingLink domain);
}

