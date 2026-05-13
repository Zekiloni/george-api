package com.zekiloni.george.platform.application.port.in.template;

import com.zekiloni.george.platform.domain.model.template.Category;

/**
 * Admin-only category management. Implementations are expected to be guarded
 * by {@code @PreAuthorize("hasRole('ADMIN')")} or equivalent — there is no
 * tenant context for these operations because categories are system-wide.
 */
public interface CategoryMutationUseCase {

    Category create(Category category);

    Category update(String id, Category patch);

    /** Refuses to delete a category that still has templates pointing at it. */
    void delete(String id);
}
