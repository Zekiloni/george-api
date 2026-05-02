package com.zekiloni.george.commerce.application.usecase;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Platform-wide defaults for service-access lifecycle. Per-offering overrides on
 * {@code Offering.renewalNoticeDays} / {@code gracePeriodDays} take precedence.
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.lifecycle")
public class LifecycleConfig {
    private int renewalNoticeDays = 3;
    private int gracePeriodDays = 1;
}
