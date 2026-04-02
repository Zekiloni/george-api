package com.zekiloni.george.infrastructure.config.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TenantIdentifierResolver implements CurrentTenantIdentifierResolver<String> {
    private final TenantContext tenantContext;

    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = tenantContext.getTenantId();

        if (tenant == null) {
            throw new HibernateException("No tenant identifier set in TenantContext");
        }

        return tenant;
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}

