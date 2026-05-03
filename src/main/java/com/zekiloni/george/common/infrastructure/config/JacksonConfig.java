package com.zekiloni.george.common.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.zekiloni.george.common.infrastructure.config.jackson.ChronoUnitJacksonMixin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.temporal.ChronoUnit;
import java.util.TimeZone;

@Configuration
public class JacksonConfig {

    /**
     * Spring Boot 4 ships both Jackson 2 and Jackson 3; we set
     * {@code spring.http.converters.preferred-json-mapper=jackson2} so HTTP
     * converters use this Jackson 2 mapper. The mixin attaches
     * {@code @JsonDeserialize}/{@code @JsonSerialize} to {@link ChronoUnit}
     * at the class level, which Jackson resolves before module-registered
     * handlers — so it overrides JavaTimeModule's toString reader.
     */
    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.addMixIn(ChronoUnit.class, ChronoUnitJacksonMixin.class);
        objectMapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        objectMapper.setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        return objectMapper;
    }
}
