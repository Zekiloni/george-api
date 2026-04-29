package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.platform.domain.model.campaign.outreach.session.UserSession;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.OutreachEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.UserSessionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserSessionEntityMapper {

    @Mapping(source = "outreach.id", target = "outreach", qualifiedByName = "outreachRefToEntity")
    UserSessionEntity toEntity(UserSession session);

    @Mapping(target = "outreach.id", source = "outreach.id")
    UserSession toDomain(UserSessionEntity entity);

    @Mapping(source = "outreach.id", target = "outreach", qualifiedByName = "outreachRefToEntity")
    void updateEntityFromDomain(UserSession domain, @MappingTarget UserSessionEntity entity);

    @Named("outreachRefToEntity")
    default OutreachEntity outreachRefToEntity(String id) {
        if (id == null) {
            return null;
        }
        return OutreachEntity.builder().id(UUID.fromString(id)).build();
    }
}
