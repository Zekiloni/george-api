package com.zekiloni.george.commerce.infrastructure.in.job.inventory;

import com.zekiloni.george.commerce.application.port.in.ServiceAccessLifecycleUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class ServiceLifecycleScheduler {
    private final ServiceAccessLifecycleUseCase useCase;

    @Scheduled(cron = "0 */5 * * * *")
    public void handle() {
        log.info("Starting service lifecycle handling at {}", OffsetDateTime.now());
        useCase.handle();
    }
}
