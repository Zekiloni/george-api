package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.out.ServiceUsageRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceUsage;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Creates the {@link ServiceUsage} row that backs quota enforcement for
 * a freshly-provisioned access. Used by every {@link com.zekiloni.george.commerce.domain.order.service.strategy.ProvisioningStrategy}
 * (SMTP, GSM, etc.) so quota tracking is consistent across service types.
 */
@Service
@RequiredArgsConstructor
public class ServiceUsageBootstrap {
    private final ServiceUsageRepositoryPort usageRepository;

    @Value("${app.usage.default-daily-limit:50000}")
    private long defaultDailyLimit;

    public ServiceUsage initialize(ServiceAccess access, OrderItem orderItem, String tenantId) {
        long totalLimit = orderItem.getUnits() != null && orderItem.getUnits() > 0
                ? orderItem.getUnits()
                : 0L; // 0 = unlimited
        ServiceUsage usage = ServiceUsage.builder()
                .serviceAccessId(access.getId())
                .tenantId(tenantId)
                .totalUsed(0)
                .totalLimit(totalLimit)
                .dailyUsed(0)
                .dailyLimit(defaultDailyLimit)
                .dailyResetDate(LocalDate.now())
                .build();
        return usageRepository.save(usage);
    }
}
