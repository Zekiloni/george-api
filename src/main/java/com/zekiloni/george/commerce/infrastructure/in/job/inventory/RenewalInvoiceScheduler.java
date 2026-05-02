package com.zekiloni.george.commerce.infrastructure.in.job.inventory;

import com.zekiloni.george.commerce.application.port.in.RenewalInvoiceUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class RenewalInvoiceScheduler {

    private final RenewalInvoiceUseCase useCase;

    /** Hourly. Renewal-invoice generation is cheap; running often catches any new accesses promptly. */
    @Scheduled(cron = "0 0 * * * *")
    public void handle() {
        log.debug("Starting renewal-invoice generation at {}", OffsetDateTime.now());
        useCase.handle();
    }
}
