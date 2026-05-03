package com.zekiloni.george.common.infrastructure.config.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.temporal.ChronoUnit;

/** Writes ChronoUnit as its name() ("MONTHS") so output matches the input format clients send. */
public class ChronoUnitNameSerializer extends JsonSerializer<ChronoUnit> {

    @Override
    public void serialize(ChronoUnit value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(value.name());
    }
}
