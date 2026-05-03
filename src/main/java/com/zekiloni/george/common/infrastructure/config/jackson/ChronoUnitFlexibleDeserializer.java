package com.zekiloni.george.common.infrastructure.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

/**
 * Accepts both the enum constant name ("MONTHS", case-insensitive) and the
 * JSR-310 toString form ("Months"). Used everywhere we expose ChronoUnit on
 * an HTTP DTO — annotated at the field level so module-registration order
 * (JavaTimeModule registers its own ChronoUnit handler) cannot shadow it.
 */
public class ChronoUnitFlexibleDeserializer extends JsonDeserializer<ChronoUnit> {

    @Override
    public ChronoUnit deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String raw = p.getValueAsString();
        if (raw == null || raw.isBlank()) return null;
        try {
            return ChronoUnit.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ignored) {
            for (ChronoUnit unit : ChronoUnit.values()) {
                if (unit.toString().equalsIgnoreCase(raw.trim())) return unit;
            }
            throw new IOException("Unknown ChronoUnit: " + raw);
        }
    }
}
