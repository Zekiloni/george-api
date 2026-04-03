package com.zekiloni.george.domain.subscription.model;

import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@Builder
public class Subscription {
    private String id;

    private OffsetDateTime validFrom;
    private OffsetDateTime validTo;

    private Plan plan;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
