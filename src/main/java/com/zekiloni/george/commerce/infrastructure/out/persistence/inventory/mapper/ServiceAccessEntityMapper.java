package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.mapper;

import com.zekiloni.george.commerce.domain.inventory.model.GsmServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.PageServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.GsmServiceAccessEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.LeadServiceAccessEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.PageServiceAccessEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceAccessEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.SmtpServiceAccessEntity;
import com.zekiloni.george.commerce.infrastructure.out.persistence.order.mapper.OrderEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.entity.PageEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(uses = {OrderEntityMapper.class})
public interface ServiceAccessEntityMapper {

    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccessEntity.class, target = LeadServiceAccess.class),
            @SubclassMapping(source = SmtpServiceAccessEntity.class, target = SmtpServiceAccess.class),
            @SubclassMapping(source = PageServiceAccessEntity.class, target = PageServiceAccess.class),
            @SubclassMapping(source = GsmServiceAccessEntity.class, target = GsmServiceAccess.class)
    })
    ServiceAccess toDomain(ServiceAccessEntity entity);

    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccess.class, target = LeadServiceAccessEntity.class),
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessEntity.class),
            @SubclassMapping(source = PageServiceAccess.class, target = PageServiceAccessEntity.class),
            @SubclassMapping(source = GsmServiceAccess.class, target = GsmServiceAccessEntity.class)
    })
    ServiceAccessEntity toEntity(ServiceAccess domain);

    @Mapping(target = "leadIds", source = "leads", qualifiedByName = "leadEntitiesToIds")
    LeadServiceAccess toDomain(LeadServiceAccessEntity entity);

    @Mapping(target = "pageIds", source = "page", qualifiedByName = "pageEntitiesToIds")
    PageServiceAccess toDomain(PageServiceAccessEntity entity);

    @Mapping(target = "leads", source = "leadIds", qualifiedByName = "leadIdsToEntities")
    LeadServiceAccessEntity toEntity(LeadServiceAccess domain);

    @Mapping(target = "page", source = "pageIds", qualifiedByName = "pageIdsToEntities")
    PageServiceAccessEntity toEntity(PageServiceAccess domain);

    List<ServiceAccessEntity> toEntity(List<ServiceAccess> serviceAccess);

    @org.mapstruct.Named("leadEntitiesToIds")
    default List<String> leadEntitiesToIds(Set<LeadEntity> leads) {
        return leads == null ? List.of()
                : leads.stream().map(LeadEntity::getId).map(UUID::toString).toList();
    }

    @org.mapstruct.Named("leadIdsToEntities")
    default Set<LeadEntity> leadIdsToEntities(List<String> leadIds) {
        if (leadIds == null || leadIds.isEmpty()) return Collections.emptySet();
        return leadIds.stream()
                .map(UUID::fromString)
                .map(id -> {
                    LeadEntity entity = new LeadEntity();
                    entity.setId(id);
                    return entity;
                })
                .collect(Collectors.toSet());
    }

    @org.mapstruct.Named("pageEntitiesToIds")
    default List<String> pageEntitiesToIds(Set<PageEntity> pages) {
        return pages == null ? List.of()
                : pages.stream().map(PageEntity::getId).map(UUID::toString).toList();
    }

    @org.mapstruct.Named("pageIdsToEntities")
    default Set<PageEntity> pageIdsToEntities(List<String> pageIds) {
        if (pageIds == null || pageIds.isEmpty()) return Collections.emptySet();
        return pageIds.stream()
                .map(UUID::fromString)
                .map(id -> {
                    PageEntity entity = new PageEntity();
                    entity.setId(id);
                    return entity;
                })
                .collect(Collectors.toSet());
    }

    @ObjectFactory
    default ServiceAccess createDomain(ServiceAccessEntity entity) {
        if (entity instanceof LeadServiceAccessEntity) {
            return new LeadServiceAccess();
        } else if (entity instanceof SmtpServiceAccessEntity) {
            return new SmtpServiceAccess();
        } else if (entity instanceof PageServiceAccessEntity) {
            return new PageServiceAccess();
        } else if (entity instanceof GsmServiceAccessEntity) {
            return new GsmServiceAccess();
        }
        throw new IllegalArgumentException("Unknown entity type: " + entity.getClass());
    }

    @ObjectFactory
    default ServiceAccessEntity createEntity(ServiceAccess domain) {
        if (domain instanceof LeadServiceAccess) {
            return new LeadServiceAccessEntity();
        } else if (domain instanceof SmtpServiceAccess) {
            return new SmtpServiceAccessEntity();
        } else if (domain instanceof PageServiceAccess) {
            return new PageServiceAccessEntity();
        } else if (domain instanceof GsmServiceAccess) {
            return new GsmServiceAccessEntity();
        }
        throw new IllegalArgumentException("Unknown domain type: " + domain.getClass());
    }
}
