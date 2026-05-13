package com.zekiloni.george.platform.application.port.in.template;

import com.zekiloni.george.platform.domain.model.template.Template;

public interface TemplateMutationUseCase {

    /** Creates a private template owned by the current tenant. tenantId/categoryId are stamped by the service. */
    Template createOwned(Template template);

    /** Admin-only: creates a public catalog template inside the given category. */
    Template createPublic(Template template, String categoryId);

    /** Updates a template. Owner can edit their own; admin can edit any. Bumps version. */
    Template update(String id, Template patch);

    /** Soft-deletes. Owner-only for private, admin-only for public. */
    void delete(String id);

    /**
     * Admin moves a tenant template into the public catalog under a category.
     * Tenant ownership is cleared.
     */
    Template publish(String id, String categoryId);
}
