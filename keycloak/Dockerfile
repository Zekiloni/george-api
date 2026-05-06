# Custom Keycloak image with the "relay" theme baked in.
#
# The theme source lives next to the API at
#   src/main/resources/theme/relay/{login,account}/
# but isn't loaded by Spring Boot — it's just versioned alongside the auth
# code. This Dockerfile copies it into Keycloak's themes dir so the running
# server picks it up.
#
# Build context: repo root (george-api). Build with:
#   docker build -f keycloak/Dockerfile -t ghcr.io/zekiloni/keycloak-george:dev .
FROM quay.io/keycloak/keycloak:26.4

COPY src/main/resources/theme/relay /opt/keycloak/themes/relay

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
