package com.zekiloni.george;

import com.zekiloni.george.support.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;

/**
 * Full-context smoke test. Inherits Postgres + Keycloak from {@link AbstractIntegrationTest},
 * exercising every bean wiring on every {@code mvn verify} run.
 *
 * <p>{@code IT}-suffixed so it runs under failsafe (verify phase). {@code mvn test} stays
 * fast (surefire only picks up {@code *Test}/{@code *Tests}).
 */
class GeorgeApiApplicationIT extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
    }
}
