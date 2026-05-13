package com.zekiloni.george.platform.application.usecase.template;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.template.TemplateQueryUseCase;
import com.zekiloni.george.platform.application.port.out.template.TemplateRepositoryPort;
import com.zekiloni.george.platform.domain.model.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateQueryService implements TemplateQueryUseCase {

    private final TemplateRepositoryPort repository;
    private final TenantContext tenantContext;

    @Override
    public Optional<Template> findById(String id) {
        return repository.findById(id)
                .filter(t -> isVisibleToCurrentTenant(t));
    }

    @Override
    public Page<Template> search(String query, String categoryId, boolean ownedOnly, Pageable pageable) {
        String currentTenant = tenantContext.getTenantId();
        return repository.search(currentTenant, query, categoryId, ownedOnly, pageable);
    }

    /**
     * Public templates are visible to everyone; private templates only to
     * their owning tenant. SYSTEM tenant sees everything (admin tooling).
     */
    private boolean isVisibleToCurrentTenant(Template t) {
        if (t.getCategoryId() != null) return true;
        String currentTenant = tenantContext.getTenantId();
        if (TenantContext.SYSTEM.equals(currentTenant)) return true;
        return t.getTenantId() != null && t.getTenantId().equals(currentTenant);
    }
}
