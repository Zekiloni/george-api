package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import com.zekiloni.george.commerce.domain.order.service.strategy.ProvisioningStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Applies a paid renewal invoice to its target ServiceAccess: extends {@code validTo}
 * by another billing period, restores ACTIVE if SUSPENDED (via strategy.resume), and
 * increments {@code renewalCount}.
 */
@Service
@Slf4j
public class ServiceAccessRenewalService {

    private final InventoryRepositoryPort repository;
    private final Map<ServiceSpecification, ProvisioningStrategy> strategiesByType;

    public ServiceAccessRenewalService(InventoryRepositoryPort repository,
                                       List<ProvisioningStrategy> provisioningStrategies) {
        this.repository = repository;
        this.strategiesByType = provisioningStrategies.stream()
                .collect(Collectors.toMap(ProvisioningStrategy::getType, Function.identity()));
    }

    @Transactional
    public void renew(String serviceAccessId) {
        ServiceAccess access = repository.findById(serviceAccessId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "ServiceAccess not found for renewal: " + serviceAccessId));

        if (access.getStatus() == ServiceStatus.TERMINATED) {
            log.warn("Ignoring renewal payment for already-terminated ServiceAccess {}", serviceAccessId);
            return;
        }

        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime base = access.getValidTo() != null && access.getValidTo().isAfter(now)
                ? access.getValidTo() : now;
        access.setValidTo(extend(access, base));

        if (access.getStatus() == ServiceStatus.SUSPENDED) {
            ProvisioningStrategy strategy = strategiesByType.get(access.getServiceSpecification());
            if (strategy != null) strategy.resume(access);
            access.setStatus(ServiceStatus.ACTIVE);
            access.setSuspendedAt(null);
        }

        access.setRenewalCount(access.getRenewalCount() + 1);
        access.setUpdatedAt(now);
        repository.save(access);

        log.info("Renewed ServiceAccess {}: validTo extended to {} (renewalCount={})",
                access.getId(), access.getValidTo(), access.getRenewalCount());
    }

    private OffsetDateTime extend(ServiceAccess access, OffsetDateTime from) {
        OrderItem item = access.getOrderItem();
        if (item == null) return from;
        Offering offering = item.getOffering();
        if (offering == null || offering.getTimeUnit() == null) return from;
        Integer units = item.getUnits();
        if (units == null || units <= 0) return from;
        long total = (long) units * Math.max(offering.getIntervalCount(), 1);
        return from.plus(total, (ChronoUnit) offering.getTimeUnit());
    }
}
