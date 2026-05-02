package com.zekiloni.george.support;

import com.github.tomakehurst.wiremock.WireMockServer;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Base class for {@code @SpringBootTest} integration tests.
 *
 * <p>Spins up the heavy stuff <b>once per JVM</b> (singleton pattern: containers stay
 * alive across the suite, dramatically faster than per-class startup):
 * <ul>
 *   <li>Real PostgreSQL via {@link PostgreSQLContainer} — {@link ServiceConnection}
 *       auto-wires {@code spring.datasource.*}. Hibernate's {@code create-drop} resets
 *       the schema on each context start, so no test pollution.</li>
 *   <li>Real Keycloak via {@link KeycloakContainer} — {@code spring.security.oauth2.*}
 *       points at the master realm.</li>
 *   <li>WireMock for BTCPay outbound calls — {@code btc-pay.api.base-url} points at it.
 *       Default behavior is empty (returns 404 for any request); tests that exercise
 *       BTCPay paths declare their own stubs via {@link #btcPayMock()}.</li>
 * </ul>
 *
 * <p><b>Why WireMock and not a real BTCPay container?</b> BTCPay's value at test time
 * is just "did we POST a well-formed invoice request and parse the response?" — pure
 * HTTP wire-format assertions. Spinning up bitcoind + nbxplorer + btcpay (3 containers,
 * ~3 minutes total) for that adds zero confidence over WireMock. See
 * {@code support/README.md} (or the test file headers) for the test-fidelity ladder.
 *
 * <p>Stalwart is deferred — same pattern when needed.
 *
 * <p><b>Multi-tenancy:</b> the app uses ThreadLocal {@code TenantContext} via
 * {@code TenantRequestFilter}. Tests that hit tenant-scoped queries should set up
 * the context themselves (typically in {@code @BeforeEach}).
 *
 * <p>Requires Docker. Tests fail fast with a clear message otherwise.
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
public abstract class AbstractIntegrationTest {

    @ServiceConnection
    protected static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:18-alpine")
            .withDatabaseName("george_test")
            .withUsername("g-user")
            .withPassword("g-pw")
            .withReuse(true);

    protected static final KeycloakContainer KEYCLOAK = new KeycloakContainer("quay.io/keycloak/keycloak:26.4")
            .withAdminUsername("admin")
            .withAdminPassword("admin")
            .withReuse(true);

    protected static final WireMockServer BTCPAY_WIREMOCK = new WireMockServer(wireMockConfig().dynamicPort());

    static {
        POSTGRES.start();
        KEYCLOAK.start();
        BTCPAY_WIREMOCK.start();
        // Ryuk reaper handles container cleanup on JVM exit; WireMock dies with the JVM.
    }

    @DynamicPropertySource
    static void registerExternalEndpoints(DynamicPropertyRegistry registry) {
        // Keycloak — production property layout.
        String issuerUri = KEYCLOAK.getAuthServerUrl() + "/realms/master";
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> issuerUri);
        registry.add("spring.security.oauth2.resourceserver.jwt.jwk-set-uri",
                () -> issuerUri + "/protocol/openid-connect/certs");

        // BTCPay — point the API client at WireMock.
        registry.add("btc-pay.api.base-url",
                () -> "http://localhost:" + BTCPAY_WIREMOCK.port() + "/api/v1");
    }

    /**
     * WireMock instance fronting BTCPay. Use {@link WireMockServer#stubFor(com.github.tomakehurst.wiremock.client.MappingBuilder)}
     * to declare expectations and {@link WireMockServer#verify(int, com.github.tomakehurst.wiremock.matching.RequestPatternBuilder)}
     * to assert calls. Stubs are reset between each test by {@link #resetBtcPayStubs()}.
     */
    protected WireMockServer btcPayMock() {
        return BTCPAY_WIREMOCK;
    }

    @BeforeEach
    void resetBtcPayStubs() {
        BTCPAY_WIREMOCK.resetAll();
    }
}
