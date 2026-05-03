package com.zekiloni.george.common.infrastructure.config.jackson;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * Mixin attached to {@link java.time.temporal.ChronoUnit} via
 * {@code ObjectMapper.addMixIn(...)} so the flexible (de)serializers apply
 * regardless of which ObjectMapper Spring MVC ends up using. Class-level
 * annotation resolution wins over module-registered handlers, so this is
 * the only way that's robust against Jackson2ObjectMapperBuilder ordering.
 */
@JsonDeserialize(using = ChronoUnitFlexibleDeserializer.class)
@JsonSerialize(using = ChronoUnitNameSerializer.class)
public abstract class ChronoUnitJacksonMixin {
}
