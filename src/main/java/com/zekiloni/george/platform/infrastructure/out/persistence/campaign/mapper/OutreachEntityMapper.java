package com.zekiloni.george.platform.infrastructure.out.persistence.campaign.mapper;

import com.zekiloni.george.platform.domain.model.campaign.outreach.Outreach;
import com.zekiloni.george.platform.infrastructure.out.persistence.campaign.entity.OutreachEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.stream.Stream;

@Mapper
public interface OutreachEntityMapper {
    OutreachEntity toEntity(Outreach outreach);

    // tenantId is inherited from TenantEntity (@MappedSuperclass). MapStruct's
    // auto-mapping of inherited fields is unreliable across versions, so map
    // it explicitly — losing it here silently breaks the anonymous-visitor
    // tenant-context pivot in UserSessionCreateService.
    @Mapping(source = "tenantId", target = "tenantId")
    @Mapping(source = "campaign.id", target = "campaignId")
    Outreach toDomain(OutreachEntity outreach);

    List<Outreach> toDomain(List<OutreachEntity> outreachEntities);

    List<OutreachEntity> toEntity(List<Outreach> outreach);

    Stream<Outreach> toDomain(Stream<OutreachEntity> outreachEntities);

    void updateEntityFromDomain(Outreach domain, @MappingTarget OutreachEntity entity);
}
