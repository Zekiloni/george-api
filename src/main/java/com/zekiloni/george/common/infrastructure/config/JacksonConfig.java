package com.zekiloni.george.common.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    /**
     * Registered as a top-level bean so Spring Boot's auto-configured
     * {@code Jackson2ObjectMapperBuilder} picks it up and applies it to every
     * ObjectMapper used by HTTP message converters. Just having it on a
     * {@code @Primary ObjectMapper} bean is not enough — the web converter
     * builds its own mapper from the builder.
     *
     * <p>The default JSR-310 deserializer reads {@link ChronoUnit} via
     * {@code toString()} (PascalCase, e.g. "Months"). This module accepts both
     * {@code "MONTHS"} (enum name, case-insensitive) and {@code "Months"}
     * (toString), and writes using the enum name to match the rest of our APIs.
     */
    @Bean
    public SimpleModule chronoUnitFlexibleModule() {
        SimpleModule module = new SimpleModule("ChronoUnitFlexibleModule");
        module.addDeserializer(ChronoUnit.class, new JsonDeserializer<>() {
            @Override
            public ChronoUnit deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                String raw = p.getValueAsString();
                if (raw == null || raw.isBlank()) return null;
                String normalized = raw.trim().toUpperCase();
                try {
                    return ChronoUnit.valueOf(normalized);
                } catch (IllegalArgumentException e) {
                    for (ChronoUnit unit : ChronoUnit.values()) {
                        if (unit.toString().equalsIgnoreCase(raw.trim())) return unit;
                    }
                    throw new IOException("Unknown ChronoUnit: " + raw);
                }
            }
        });
        module.addSerializer(ChronoUnit.class, new JsonSerializer<>() {
            @Override
            public void serialize(ChronoUnit value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(value.name());
            }
        });
        return module;
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.registerModule(chronoUnitFlexibleModule());
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }
}
