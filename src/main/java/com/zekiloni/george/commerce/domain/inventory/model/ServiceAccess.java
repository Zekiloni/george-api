package com.zekiloni.george.commerce.domain.inventory.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.order.model.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public abstract class ServiceAccess {
    private String id;
    private ServiceSpecification serviceSpecification;
    private OrderItem orderItem;
    private List<Characteristic> characteristic;
    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;
    private ServiceStatus status;

    /**
     * When the service was last suspended (null for ACTIVE/TERMINATED).
     */
    private OffsetDateTime suspendedAt;
    /**
     * When the service was permanently terminated (null until TERMINATED).
     */
    private OffsetDateTime terminatedAt;
    /**
     * How many times this access has been renewed (incremented on RENEWAL invoice PAID).
     */
    private int renewalCount;
    /**
     * If true, the renewal generator will skip this access — it will expire and terminate normally.
     */
    private boolean cancelAtPeriodEnd;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private String tenantId;
    private String gatewayId;


    public Optional<Integer> getDailyLimit() {
        return characteristicNumber(ServiceAccessCharacteristicKey.DAILY_LIMIT).map(Number::intValue);
    }

    public Optional<Long> getMessageQuota() {
        return characteristicNumber(ServiceAccessCharacteristicKey.MESSAGE_QUOTA).map(Number::longValue);
    }

    private Optional<Number> characteristicNumber(ServiceAccessCharacteristicKey key) {
        if (characteristic == null) return Optional.empty();
        return characteristic.stream()
                .filter(c -> key.getValue().equals(c.getName()))
                .findFirst()
                .map(Characteristic::getValue)
                .map(v -> v instanceof Number n ? n : Long.parseLong(v.toString()));
    }
}
