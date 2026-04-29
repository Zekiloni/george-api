package com.zekiloni.george.commerce.domain.order.model;

import com.zekiloni.george.common.domain.model.Characteristic;
import com.zekiloni.george.commerce.domain.catalog.model.DurationUnit;
import com.zekiloni.george.commerce.domain.catalog.model.Offering;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@Builder
public class OrderItem {
    private String id;
    private Offering offering;
    private Integer quantity;
    private Integer duration;
    private DurationUnit durationUnit;
    private List<Characteristic> characteristic;
    private String gatewayId;  // ID of the gateway used for provisioning
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

