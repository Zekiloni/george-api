package com.zekiloni.george.common.infrastructure.config.tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Slf4j
@Component
//@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TenantContext {
    public static final String SYSTEM = "system";
    private static final ThreadLocal<String> tenantHolder =
            ThreadLocal.withInitial(() -> SYSTEM);


    public String getTenantId() {
        return tenantHolder.get();
    }

    public void setTenantId(String tenantId) {
        tenantHolder.set(tenantId);
    }

    public void clear() {
        tenantHolder.remove();
    }

    public boolean isSystem() {
        String tenantId = getTenantId();
        return tenantId != null && tenantId.equals(SYSTEM);
    }
}

