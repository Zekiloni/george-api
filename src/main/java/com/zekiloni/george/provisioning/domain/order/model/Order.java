package com.zekiloni.george.provisioning.domain.order.model;

import com.zekiloni.george.provisioning.domain.catalog.model.DurationUnit;
import com.zekiloni.george.provisioning.domain.catalog.model.Offering;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class Order {
    private String id;

    private Offering offering;
    private Integer quantity;
    private Integer duration;
    private DurationUnit durationUnit;

    private OrderStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

