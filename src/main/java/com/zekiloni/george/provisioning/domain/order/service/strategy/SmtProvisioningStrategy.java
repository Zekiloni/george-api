package com.zekiloni.george.provisioning.domain.order.service.strategy;

import com.zekiloni.george.provisioning.application.port.in.ServiceAccessCreateUseCase;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.provisioning.domain.inventory.model.SmtpServiceAccess;
import com.zekiloni.george.provisioning.domain.order.model.Order;
import com.zekiloni.george.provisioning.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class SmtProvisioningStrategy implements ProvisioningStrategy {
    private final ServiceAccessCreateUseCase serviceAccessCreateUseCase;

    @Override
    public ServiceSpecification getType() {
        return ServiceSpecification.SMTP;
    }

    @Override
    public void provision(Order order, OrderItem orderItem) {
        // todo: contact SMT provider to provision the service access for the order item
        SmtpServiceAccess build = SmtpServiceAccess.builder()
                .validFrom(OffsetDateTime.now())
                .validTo(getValidTo(orderItem))
                .serviceSpecification(getType())
                .order(order)
                .tenantId(order.getTenantId())
                .build();

        serviceAccessCreateUseCase.create(build);
    }

    @Override
    public void deprovision(OrderItem order) {

    }

    private OffsetDateTime getValidTo(OrderItem orderItem) {
        OffsetDateTime now = OffsetDateTime.now();
        return switch (orderItem.getDurationUnit()) {
            case HOURS -> now.plusHours(orderItem.getDuration());
            case DAYS -> now.plusDays(orderItem.getDuration());
            case MONTHS -> now.plusMonths(orderItem.getDuration());
            case YEARS -> now.plusYears(orderItem.getDuration());
            default -> throw new IllegalArgumentException("Unsupported duration unit: " + orderItem.getDurationUnit());
        };
    }
}
