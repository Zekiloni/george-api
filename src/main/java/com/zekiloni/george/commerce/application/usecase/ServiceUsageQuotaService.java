package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.out.ServiceUsageRepositoryPort;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceUsage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * Quota enforcement against {@link ServiceUsage} rows. One DB lock per
 * batch (not per message) — so 30k-message campaigns reserve in one
 * transactional update. Limits of {@code 0} mean "unlimited".
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceUsageQuotaService {
    private final ServiceUsageRepositoryPort usageRepository;

    /**
     * Reserve up to {@code requested} units against the access's usage row.
     *
     * @return granted count (0 ≤ granted ≤ requested), capped by remaining
     *         total and remaining daily allowance. The corresponding usage
     *         row is updated and persisted within this transaction.
     */
    @Transactional
    public int reserve(String serviceAccessId, int requested) {
        if (requested <= 0) return 0;

        ServiceUsage usage = usageRepository.lockByServiceAccessId(serviceAccessId)
                .orElseThrow(() -> new IllegalStateException(
                        "No ServiceUsage row for serviceAccessId " + serviceAccessId));

        rollDailyIfNeeded(usage);

        long totalRemaining = usage.getTotalLimit() <= 0
                ? Long.MAX_VALUE
                : Math.max(0, usage.getTotalLimit() - usage.getTotalUsed());
        long dailyRemaining = usage.getDailyLimit() <= 0
                ? Long.MAX_VALUE
                : Math.max(0, usage.getDailyLimit() - usage.getDailyUsed());

        int granted = (int) Math.min(requested, Math.min(totalRemaining, dailyRemaining));
        if (granted == 0) {
            log.warn("Quota exhausted for ServiceAccess {}: total {}/{}, daily {}/{}",
                    serviceAccessId, usage.getTotalUsed(), usage.getTotalLimit(),
                    usage.getDailyUsed(), usage.getDailyLimit());
            return 0;
        }

        usage.setTotalUsed(usage.getTotalUsed() + granted);
        usage.setDailyUsed(usage.getDailyUsed() + granted);
        usageRepository.save(usage);

        log.info("Reserved {} of {} on ServiceAccess {} (total {}/{}, daily {}/{})",
                granted, requested, serviceAccessId,
                usage.getTotalUsed(), usage.getTotalLimit(),
                usage.getDailyUsed(), usage.getDailyLimit());
        return granted;
    }

    /** Refund unused capacity (e.g. messages that failed at SMTP send). */
    @Transactional
    public void release(String serviceAccessId, int amount) {
        if (amount <= 0) return;
        ServiceUsage usage = usageRepository.lockByServiceAccessId(serviceAccessId)
                .orElseThrow(() -> new IllegalStateException(
                        "No ServiceUsage row for serviceAccessId " + serviceAccessId));
        usage.setTotalUsed(Math.max(0, usage.getTotalUsed() - amount));
        usage.setDailyUsed(Math.max(0, usage.getDailyUsed() - amount));
        usageRepository.save(usage);
        log.info("Released {} units on ServiceAccess {}", amount, serviceAccessId);
    }

    private static void rollDailyIfNeeded(ServiceUsage usage) {
        LocalDate today = LocalDate.now();
        if (usage.getDailyResetDate() == null || usage.getDailyResetDate().isBefore(today)) {
            usage.setDailyUsed(0);
            usage.setDailyResetDate(today);
        }
    }
}
