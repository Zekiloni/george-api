package com.zekiloni.george.commerce.domain.order.service.strategy;

import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceAccess;
import com.zekiloni.george.commerce.domain.order.model.Order;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;

public interface ProvisioningStrategy {
    ServiceSpecification getType();

    void provision(Order order, OrderItem orderItem);

    void deprovision(ServiceAccess access);

    /**
     * Lock the service so the tenant can't use it, but keep all data intact.
     * Default no-op — override per service type if there's a remote gateway to disable.
     */
    default void suspend(ServiceAccess access) {
        // no-op default; override where suspension has remote effects (e.g. SMTP account lock)
    }

    /**
     * Restore a suspended service to ACTIVE. Mirror of {@link #suspend}.
     */
    default void resume(ServiceAccess access) {
        // no-op default
    }

    /**
     * Time-based offerings ({@link Offering#getTimeUnit()} non-null) extend the resulting
     * {@code ServiceAccess.validTo}. Quota-based offerings return {@code null} —
     * those services are bounded by units consumed, not time elapsed.
     */
    default OffsetDateTime getValidTo(OrderItem orderItem) {
        Offering offering = orderItem.getOffering();
        if (offering == null) return null;
        ChronoUnit timeUnit = offering.getTimeUnit();
        if (timeUnit == null) return null;

        Integer units = orderItem.getUnits();
        if (units == null || units <= 0) return null;

        long total = (long) units * Math.max(offering.getIntervalCount(), 1);
        return OffsetDateTime.now().plus(total, timeUnit);
    }
}
