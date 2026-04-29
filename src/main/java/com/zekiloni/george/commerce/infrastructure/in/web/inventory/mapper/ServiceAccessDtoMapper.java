package com.zekiloni.george.commerce.infrastructure.in.web.inventory.mapper;

import com.zekiloni.george.commerce.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.PageServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto.LeadServiceAccessDto;
import com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto.PageServiceAccessDto;
import com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto.ServiceAccessDto;
import com.zekiloni.george.commerce.infrastructure.in.web.inventory.dto.SmtpServiceAccessDto;
import com.zekiloni.george.commerce.infrastructure.in.web.order.mapper.OrderDtoMapper;
import org.mapstruct.*;

@Mapper(uses = {OrderDtoMapper.class})
public interface ServiceAccessDtoMapper {

    @SubclassMappings({
            @SubclassMapping(source = LeadServiceAccess.class, target = LeadServiceAccessDto.class),
            @SubclassMapping(source = SmtpServiceAccess.class, target = SmtpServiceAccessDto.class),
            @SubclassMapping(source = PageServiceAccess.class, target = PageServiceAccessDto.class)
    })
    @Mapping(source = "orderItem.id", target = "orderItemId")
    ServiceAccessDto toDto(ServiceAccess serviceAccess);

    @InheritInverseConfiguration
    @ObjectFactory
    default ServiceAccess createServiceAccess(ServiceAccessDto dto) {
        return switch (dto) {
            case LeadServiceAccessDto _ -> new LeadServiceAccess();
            case SmtpServiceAccessDto _ -> new SmtpServiceAccess();
            case PageServiceAccessDto _ -> new PageServiceAccess();
            default -> throw new IllegalArgumentException("Unknown ServiceAccessDto type: " + dto.getClass().getName());
        };
    }
}



