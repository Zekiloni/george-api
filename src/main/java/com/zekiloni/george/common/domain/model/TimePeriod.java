package com.zekiloni.george.common.domain.model;

import lombok.*;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimePeriod {
    private OffsetDateTime startDateTime;
    private OffsetDateTime endDateTime;

    public LocalDate getStartDate() {
        return startDateTime != null ? startDateTime.toLocalDate() : null;
    }

    public LocalDate getEndDate() {
        return endDateTime != null ? endDateTime.toLocalDate() : null;
    }

    public boolean isValid() {
        return startDateTime != null && endDateTime != null && !endDateTime.isBefore(startDateTime);
    }

    public boolean contains(LocalDate date) {
        return !date.isBefore(getStartDate()) && !date.isAfter(getEndDate());
    }

    public boolean overlaps(TimePeriod other) {
        return !this.endDateTime.isBefore(other.startDateTime) && !other.endDateTime.isBefore(this.startDateTime);
    }

    public long getDurationInDays() {
        return java.time.temporal.ChronoUnit.DAYS.between(startDateTime, endDateTime) + 1;
    }
}

