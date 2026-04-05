package com.zekiloni.george.common.infrastructure.out.persistence.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Converter
public class OffsetDateTimeConverter implements AttributeConverter<OffsetDateTime, LocalDateTime> {
    @Override
    public LocalDateTime convertToDatabaseColumn(OffsetDateTime attribute) {
        return attribute != null ? attribute.toLocalDateTime() : null;
    }

    @Override
    public OffsetDateTime convertToEntityAttribute(LocalDateTime dbData) {
        return dbData != null ? dbData.atOffset(ZoneOffset.UTC) : null;
    }
}