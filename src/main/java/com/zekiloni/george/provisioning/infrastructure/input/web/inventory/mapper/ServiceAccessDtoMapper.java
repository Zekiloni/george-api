package com.zekiloni.george.provisioning.infrastructure.input.web.inventory.mapper;

import com.zekiloni.george.provisioning.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.provisioning.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto.LeadServiceAccessDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto.ServiceAccessDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.inventory.dto.SmtpServiceAccessDto;
import com.zekiloni.george.provisioning.infrastructure.input.web.order.mapper.OrderDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.SubclassMapping;
import org.mapstruct.SubclassMappings;

@Mapper(uses = {OrderDtoMapper.class})
public interface ServiceAccessDtoMapper {
    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccess.class, target = LeadServiceAccessDto.class),
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessDto.class)
    })
    @Mapping(source = "orderItem.id", target = "orderItemId")
    ServiceAccessDto toDto(ServiceAccess serviceAccess);
}



