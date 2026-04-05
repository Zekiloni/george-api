package com.zekiloni.george.domain.billing.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class Order {
    private String id;

    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    private Offering offering;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}

