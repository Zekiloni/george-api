package com.zekiloni.george.infrastructure.output.persistence.common.entity;


import jakarta.persistence.Column;

import java.time.OffsetDateTime;

public class TemporalEntity extends BaseEntity {
    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo;
}
