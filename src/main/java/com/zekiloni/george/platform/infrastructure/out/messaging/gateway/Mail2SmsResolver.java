package com.zekiloni.george.platform.infrastructure.out.messaging.gateway;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Maps a {@code (country, carrier)} pair to the mail2SMS domain used as the
 * recipient address for SMTP-to-SMS gateways. Configured via
 * {@code app.mail2sms.mappings.<country>.<carrier>} — country and carrier are
 * matched case-insensitively. MVNOs share their host carrier's domain (Fido and
 * Virgin both ride Bell, so both map to {@code txt.bell.ca}).
 */
@Getter
@Component
@ConfigurationProperties(prefix = "app.mail2sms")
@Slf4j
public class Mail2SmsResolver {
    private Map<String, Map<String, String>> mappings = Collections.emptyMap();

    public void setMappings(Map<String, Map<String, String>> mappings) {
        this.mappings = mappings == null ? Collections.emptyMap() : mappings;
    }

    public Optional<String> resolve(String country, String carrier) {
        if (country == null || carrier == null) return Optional.empty();
        Map<String, String> byCarrier = mappings.get(country.toLowerCase(Locale.ROOT));
        if (byCarrier == null) return Optional.empty();
        return Optional.ofNullable(byCarrier.get(carrier.toLowerCase(Locale.ROOT)));
    }
}
