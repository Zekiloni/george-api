package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.CampaignEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CampaignEntityMapper {

    @Mapping(source = "page.id", target = "pageId")
    @Mapping(source = "serviceAccess.id", target = "serviceAccessId")
    CampaignEntity toEntity(Campaign campaign);

    @Mapping(target = "page", source = "pageId", qualifiedByName = "uuidToRef")
    @Mapping(target = "serviceAccess", source = "serviceAccessId", qualifiedByName = "uuidToRef")
    Campaign toDomain(CampaignEntity campaignEntity);

    @Mapping(source = "page.id", target = "pageId")
    @Mapping(source = "serviceAccess.id", target = "serviceAccessId")
    void updateEntityFromDomain(Campaign domain, @MappingTarget CampaignEntity entity);

    @Named("uuidToRef")
    default Ref uuidToRef(UUID id) {
        if (id == null) {
            return null;
        }
        return Ref.builder().id(id.toString()).build();
    }
}
