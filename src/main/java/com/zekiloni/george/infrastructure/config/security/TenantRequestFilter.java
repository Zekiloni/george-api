package com.zekiloni.george.infrastructure.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TenantRequestFilter extends OncePerRequestFilter {
    private final TenantContext tenantContext;

    private static final String ORGANIZATION_CLAIM = "organization";
    public static final String ID = "id";

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            Jwt jwt = jwtAuth.getToken();
            extractOrganizationId(jwt).ifPresent(tenantContext::setTenantId);
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            tenantContext.clear();
        }
    }

    @Override
    protected boolean shouldNotFilterErrorDispatch() {
        return true;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return true;
    }

    private Optional<String> extractOrganizationId(Jwt jwt) {
        Object organizationObj = jwt.getClaim(ORGANIZATION_CLAIM);

        if (!(organizationObj instanceof Map<?, ?> organizations) || organizations.isEmpty()) {
            return Optional.empty();
        }

        return organizations.values().stream()
                .filter(Map.class::isInstance)
                .map(v -> ((Map<?, ?>) v).get(ID))
                .filter(id -> id != null && !id.toString().isBlank())
                .map(Object::toString)
                .findFirst();
    }
}

