package com.zekiloni.george.common.infrastructure.in.web.dto;

import java.time.OffsetDateTime;

public record TimePeriodDto(
        OffsetDateTime startDateTime,
        OffsetDateTime endDateTime
) {
}

