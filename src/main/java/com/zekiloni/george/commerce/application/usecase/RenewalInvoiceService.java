package com.zekiloni.george.commerce.application.usecase;

import com.zekiloni.george.commerce.application.port.in.InvoiceCreateUseCase;
import com.zekiloni.george.commerce.application.port.in.RenewalInvoiceUseCase;
import com.zekiloni.george.commerce.application.port.out.InventoryRepositoryPort;
import com.zekiloni.george.commerce.application.port.out.InvoiceRepositoryPort;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Generates RENEWAL invoices for ServiceAccess records nearing expiry.
 * Skips access flagged {@code cancelAtPeriodEnd} or already covered by a
 * pending renewal invoice. Per-offering {@code renewalNoticeDays} overrides the
 * platform default in {@link LifecycleConfig}.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RenewalInvoiceService implements RenewalInvoiceUseCase {

    private final InventoryRepositoryPort inventoryRepository;
    private final InvoiceRepositoryPort invoiceRepository;
    private final InvoiceCreateUseCase invoiceCreateUseCase;
    private final LifecycleConfig lifecycleConfig;

    @Override
    @Transactional
    public void handle() {
        OffsetDateTime now = OffsetDateTime.now();
        List<ServiceAccess> renewable = inventoryRepository
                .findRenewable(now, lifecycleConfig.getRenewalNoticeDays());

        for (ServiceAccess access : renewable) {
            try {
                if (!withinNoticeWindow(now, access)) continue;
                if (invoiceRepository.hasOpenRenewalForServiceAccess(access.getId())) continue;

                invoiceCreateUseCase.createRenewal(access);
                log.info("Issued renewal invoice for ServiceAccess {} (validTo={})",
                        access.getId(), access.getValidTo());
            } catch (Exception e) {
                log.error("Failed to issue renewal invoice for ServiceAccess {}: {}",
                        access.getId(), e.getMessage(), e);
            }
        }
    }

    /** Per-offering override beats the platform default. */
    private boolean withinNoticeWindow(OffsetDateTime now, ServiceAccess access) {
        if (access.getValidTo() == null) return false;
        Offering offering = access.getOrderItem() != null ? access.getOrderItem().getOffering() : null;
        int notice = (offering != null && offering.getRenewalNoticeDays() != null)
                ? offering.getRenewalNoticeDays()
                : lifecycleConfig.getRenewalNoticeDays();
        OffsetDateTime horizon = now.plusDays(Math.max(notice, 0));
        return access.getValidTo().isAfter(now) && !access.getValidTo().isAfter(horizon);
    }
}
