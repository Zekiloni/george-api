package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.common.domain.model.Ref;
import com.zekiloni.george.platform.domain.model.campaign.Campaign;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.CampaignEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CampaignEntityMapper {

    @Mapping(source = "flow", target = "flowPageIds", qualifiedByName = "refsToUuids")
    @Mapping(source = "serviceAccess.id", target = "serviceAccessId")
    CampaignEntity toEntity(Campaign campaign);

    @Mapping(target = "flow", source = "flowPageIds", qualifiedByName = "uuidsToRefs")
    @Mapping(target = "serviceAccess", source = "serviceAccessId", qualifiedByName = "uuidToRef")
    Campaign toDomain(CampaignEntity campaignEntity);

    @Mapping(source = "flow", target = "flowPageIds", qualifiedByName = "refsToUuids")
    @Mapping(source = "serviceAccess.id", target = "serviceAccessId")
    void updateEntityFromDomain(Campaign domain, @MappingTarget CampaignEntity entity);

    @Named("uuidToRef")
    default Ref uuidToRef(UUID id) {
        return id == null ? null : Ref.builder().id(id.toString()).build();
    }

    @Named("uuidsToRefs")
    default List<Ref> uuidsToRefs(List<UUID> ids) {
        return ids == null ? List.of()
                : ids.stream().map(this::uuidToRef).toList();
    }

    @Named("refsToUuids")
    default List<UUID> refsToUuids(List<Ref> refs) {
        return refs == null ? List.of()
                : refs.stream()
                        .filter(r -> r != null && r.getId() != null)
                        .map(r -> UUID.fromString(r.getId()))
                        .toList();
    }
}
