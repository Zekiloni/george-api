package com.zekiloni.george.platform.application.usecase.template;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.in.template.TemplateMutationUseCase;
import com.zekiloni.george.platform.application.port.out.template.CategoryRepositoryPort;
import com.zekiloni.george.platform.application.port.out.template.TemplateRepositoryPort;
import com.zekiloni.george.platform.domain.model.template.Template;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class TemplateMutationService implements TemplateMutationUseCase {

    private final TemplateRepositoryPort templateRepository;
    private final CategoryRepositoryPort categoryRepository;
    private final TenantContext tenantContext;

    @Override
    public Template createOwned(Template template) {
        String currentTenant = requireTenant();
        // Force the (categoryId IS NULL, tenantId IS NOT NULL) branch of the
        // CHECK constraint regardless of what the caller passed.
        template.setId(null);
        template.setCategoryId(null);
        template.setTenantId(currentTenant);
        template.setVersion(1);
        template.setUsageCount(0);
        return templateRepository.save(template);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Template createPublic(Template template, String categoryId) {
        if (categoryId == null || categoryRepository.findById(categoryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required and must exist");
        }
        template.setId(null);
        template.setCategoryId(categoryId);
        template.setTenantId(null);
        template.setVersion(1);
        template.setUsageCount(0);
        return templateRepository.save(template);
    }

    @Override
    public Template update(String id, Template patch) {
        Template existing = templateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));
        requireCanMutate(existing);

        if (patch.getName() != null)         existing.setName(patch.getName());
        if (patch.getDescription() != null)  existing.setDescription(patch.getDescription());
        if (patch.getThumbnailUrl() != null) existing.setThumbnailUrl(patch.getThumbnailUrl());
        if (patch.getSteps() != null)        existing.setSteps(patch.getSteps());
        if (patch.getVariables() != null)    existing.setVariables(patch.getVariables());

        // Bump version on every successful edit. Campaigns that cached
        // sourceTemplateVersion will see a new value and can offer to sync.
        existing.setVersion(existing.getVersion() + 1);
        return templateRepository.save(existing);
    }

    @Override
    public void delete(String id) {
        Template existing = templateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));
        requireCanMutate(existing);
        templateRepository.delete(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public Template publish(String id, String categoryId) {
        if (categoryId == null || categoryRepository.findById(categoryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "categoryId is required and must exist");
        }
        Template existing = templateRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found"));
        existing.setCategoryId(categoryId);
        existing.setTenantId(null);
        existing.setVersion(existing.getVersion() + 1);
        return templateRepository.save(existing);
    }

    /**
     * Mutation authorization: owner can edit their private template; admin
     * can edit anything. Public templates can only be edited by admins —
     * Spring's @PreAuthorize doesn't easily expose the role check from a
     * called method, so this guard checks ownership and lets the controller
     * layer's @PreAuthorize on admin endpoints handle the rest.
     */
    private void requireCanMutate(Template template) {
        String currentTenant = tenantContext.getTenantId();
        if (TenantContext.SYSTEM.equals(currentTenant)) return; // admin/system tooling
        if (template.getCategoryId() != null) {
            // Public template — only ADMIN endpoints allow editing.
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Public templates can only be edited by admins");
        }
        if (!template.getTenantId().equals(currentTenant)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Template not found");
        }
    }

    private String requireTenant() {
        String currentTenant = tenantContext.getTenantId();
        if (currentTenant == null || TenantContext.SYSTEM.equals(currentTenant)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tenant context required");
        }
        return currentTenant;
    }
}
