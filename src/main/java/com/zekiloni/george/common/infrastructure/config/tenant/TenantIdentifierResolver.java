package com.zekiloni.george.common.infrastructure.config.tenant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.ObjectProvider;
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
            log.warn("No tenant identifier found in TenantContext. Defaulting to 'system'.");
            tenant = TenantContext.SYSTEM;
        }

        return tenant;
    }

    @Override
    public boolean isRoot(String tenantId) {
        return TenantContext.SYSTEM.equals(tenantId);
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}

