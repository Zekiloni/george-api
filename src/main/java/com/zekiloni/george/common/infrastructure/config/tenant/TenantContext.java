package com.zekiloni.george.common.infrastructure.config.tenant;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Getter
@Setter
@Slf4j
@Component
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class TenantContext {
    public static final String SYSTEM = "system";

    private String tenantId;

    public void clear() {
        this.tenantId = null;
    }

    public boolean isSystem() {
        return this.tenantId != null && this.tenantId.equals(SYSTEM);
    }
}

