package com.zekiloni.george.common.infrastructure.config.tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class TenantRequestFilter extends OncePerRequestFilter {
    public static final String X_ADMIN = "X-Admin-Access";
    private final TenantContext tenantContext;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth instanceof JwtAuthenticationToken jwtAuth) {
                Jwt jwt = jwtAuth.getToken();

                String tenantId = isAdminRequest(request, jwtAuth) ? TenantContext.SYSTEM : jwt.getSubject();
                tenantContext.setTenantId(tenantId);
            }

            filterChain.doFilter(request, response);
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

    private boolean isAdminRequest(HttpServletRequest request, JwtAuthenticationToken jwtAuth) {
        String header = request.getHeader(X_ADMIN);
        boolean requestedAdmin = header != null && header.equalsIgnoreCase(BooleanUtils.TRUE);

        if (!requestedAdmin) return false;

        boolean hasAdminRole = jwtAuth.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_admin"));

        if (!hasAdminRole) {
            throw new AccessDeniedException("User does not have admin privileges");
        }

        return true;
    }
}

