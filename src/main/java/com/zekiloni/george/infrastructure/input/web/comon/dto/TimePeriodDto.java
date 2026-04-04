package com.zekiloni.george.infrastructure.input.web.comon.dto;

import java.time.OffsetDateTime;

public record TimePeriodDto(
        OffsetDateTime startDateTime,
        OffsetDateTime endDateTime
) {
}

