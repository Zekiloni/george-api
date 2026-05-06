# Custom Keycloak image with the "relay" theme + Keywind login provider
# baked in.
#
#   resources/keycloak/keywind.jar           — Keywind theme provider (login)
#   resources/keycloak/theme/relay/          — Our theme (parent=keywind for
#                                              login, parent=keycloak.v3 for
#                                              account)
#
# Build context: repo root (george-api). Build with:
#   docker build -f resources/keycloak/Dockerfile \
#                -t ghcr.io/zekiloni/keycloak-george:dev .
FROM quay.io/keycloak/keycloak:26.4

# Provider JAR — gives us `parent=keywind` resolution. Bumping Keywind:
# replace this file with a newer release JAR from
# https://github.com/lukin/keywind/releases and rebuild.
COPY resources/keycloak/keywind.jar /opt/keycloak/providers/keywind.jar

COPY resources/keycloak/theme/relay /opt/keycloak/themes/relay

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]

