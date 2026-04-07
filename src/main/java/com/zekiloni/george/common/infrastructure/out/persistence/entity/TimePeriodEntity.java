package com.zekiloni.george.common.infrastructure.out.persistence.entity;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Embeddable
@Getter
@Setter
public class TimePeriodEntity {
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;
}

