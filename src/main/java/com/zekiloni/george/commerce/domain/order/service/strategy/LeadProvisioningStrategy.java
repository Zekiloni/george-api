package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.LeadServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.platform.application.port.out.lead.LeadRepositoryPort;
import com.zekiloni.george.platform.application.port.out.lead.LeadRepositoryPort.LeadFilter;
import com.zekiloni.george.platform.domain.model.lead.Lead;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class LeadProvisioningStrategy implements ProvisioningStrategy {
    static final String COUNTRY = "country";
    static final String AREA_CODE = "areaCode";
    static final String REGION_CODE = "regionCode";
    static final String CARRIER = "carrier";
    static final String LOCATION = "location";

    private final LeadRepositoryPort leadRepository;
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.LEADS;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        int units = orderItem.getUnits() == null ? 0 : orderItem.getUnits();
        LeadFilter filter = buildFilter(orderItem);
        List<Lead> leads = leadRepository.findRandom(filter, units);

        if (leads.size() < units) {
            log.warn("Lead pool delivered {} of {} requested for order item {} (filter={})",
                    leads.size(), units, orderItem.getId(), filter);
        }

        LeadServiceAccess access = LeadServiceAccess.builder()
                .serviceSpecification(getType())
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .leads(leads)
                .status(ServiceStatus.ACTIVE)
                .characteristic(orderItem.getCharacteristic())
                .orderItem(orderItem)
                .tenantId(order.getTenantId())
                .build();

        serviceAccessCreateUseCase.create(access);
    }

    @Override
    public void deprovision(ServiceAccess access) {
        if (access instanceof LeadServiceAccess lead) {
            lead.setLeads(List.of());
        }
    }

    private LeadFilter buildFilter(OrderItem item) {
        return new LeadFilter(
                read(item, COUNTRY),
                read(item, AREA_CODE),
                read(item, REGION_CODE),
                read(item, CARRIER),
                read(item, LOCATION));
    }

    private static String read(OrderItem item, String name) {
        List<Characteristic> chars = item.getCharacteristic();
        if (chars == null) return null;
        return chars.stream()
                .filter(c -> name.equals(c.getName()))
                .findFirst()
                .map(Characteristic::getValue)
                .map(Object::toString)
                .filter(s -> !s.isBlank())
                .orElse(null);
    }
}
