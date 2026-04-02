package com.zekiloni.george.infrastructure.integration.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NonNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    public static final String REALM_ACCESS = "realm_access";
    public static final String RESOURCE_ACCESS = "resource_access";
    public static final String ROLE = "ROLE_";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Collection<GrantedAuthority> convert(@NonNull Jwt jwt) {
        List<String> realmRoles = extractRealmRoles(jwt);
        Collection<GrantedAuthority> authorities = new ArrayList<>(realmRoles.stream()
                .map(role -> new SimpleGrantedAuthority(ROLE + role))
                .toList());

        Map<String, List<String>> resourceRoles = extractResourceRoles(jwt);
        resourceRoles.forEach((resource, roles) -> {
            authorities.addAll(roles.stream()
                    .map(role -> new SimpleGrantedAuthority(ROLE + resource + "_" + role))
                    .toList());
        });

        return authorities;
    }

    private List<String> extractRealmRoles(Jwt jwt) {
        try {
            Object realmAccessObj = jwt.getClaim(REALM_ACCESS);
            if (realmAccessObj == null) {
                return Collections.emptyList();
            }

            RealmAccess realmAccess = objectMapper.convertValue(
                    realmAccessObj,
                    RealmAccess.class
            );

            return Optional.ofNullable(realmAccess)
                    .map(RealmAccess::getRoles)
                    .orElse(Collections.emptyList());
        } catch (IllegalArgumentException e) {
            return Collections.emptyList();
        }
    }

    private Map<String, List<String>> extractResourceRoles(Jwt jwt) {
        try {
            Object resourceAccessObj = jwt.getClaim(RESOURCE_ACCESS);
            if (resourceAccessObj == null) {
                return Collections.emptyMap();
            }

            Map<String, ResourceAccess> resourceAccess = objectMapper.convertValue(
                    resourceAccessObj,
                    new TypeReference<Map<String, ResourceAccess>>() {}
            );

            return resourceAccess.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> Optional.ofNullable(entry.getValue())
                                    .map(ResourceAccess::getRoles)
                                    .orElse(Collections.emptyList())
                    ));
        } catch (IllegalArgumentException e) {
            return Collections.emptyMap();
        }
    }

    @Data
    private static class RealmAccess {
        private List<String> roles = new ArrayList<>();
    }

    @Data
    private static class ResourceAccess {
        private List<String> roles = new ArrayList<>();
    }
}

