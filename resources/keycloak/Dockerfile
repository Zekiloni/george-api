# Custom Keycloak image with the "relay" theme baked in.
#
# Theme source lives at resources/keycloak/theme/relay/{login,account}/ —
# outside the Java classpath so it doesn't end up in the Spring Boot JAR,
# but still version-controlled in this repo so realm UI evolves with the
# auth code.
#
# Build context: repo root (george-api). Build with:
#   docker build -f resources/keycloak/Dockerfile -t ghcr.io/zekiloni/keycloak-george:dev .
FROM quay.io/keycloak/keycloak:26.4

COPY resources/keycloak/theme/relay /opt/keycloak/themes/relay

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
