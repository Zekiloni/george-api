package com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.mapper;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.mapper.LeadEntityMapper;
import com.zekiloni.george.provisioning.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.PageServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity.LeadServiceAccessEntity;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity.PageServiceAccessEntity;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity.ServiceAccessEntity;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity.SmtpServiceAccessEntity;
import com.zekiloni.george.provisioning.infrastructure.output.persistence.order.mapper.OrderEntityMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ObjectFactory;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

import java.util.List;

@Mapper(uses = {OrderEntityMapper.class, LeadEntityMapper.class})
public interface ServiceAccessEntityMapper {
    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccessEntity.class, target = LeadServiceAccess.class),
            @SubclassMapping(source = SmtpServiceAccessEntity.class, target = SmtpServiceAccess.class)
    })
    ServiceAccess toDomain(ServiceAccessEntity entity);

    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccess.class, target = LeadServiceAccessEntity.class),
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessEntity.class)
    })
    ServiceAccessEntity toEntity(ServiceAccess domain);

    List<ServiceAccessEntity> toEntity(List<ServiceAccess> serviceAccess);

    @ObjectFactory
    default ServiceAccess createDomain(ServiceAccessEntity entity) {
        if (entity instanceof LeadServiceAccessEntity) {
            return new LeadServiceAccess();
        } else if (entity instanceof SmtpServiceAccessEntity) {
            return new SmtpServiceAccess();
        } else if (entity instanceof PageServiceAccessEntity) {
            return new PageServiceAccess();
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
        }
        throw new IllegalArgumentException("Unknown domain type: " + domain.getClass());
    }
}
