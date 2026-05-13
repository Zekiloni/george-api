package com.zekiloni.george.platform.application.port.out.template;

import com.zekiloni.george.platform.domain.model.template.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryRepositoryPort {

    /** All non-deleted categories, sorted by (parentId, sortOrder, name). */
    List<Category> findAll();

    Optional<Category> findById(String id);

    Optional<Category> findBySlug(String parentId, String slug);

    Category save(Category category);

    /** Soft-deletes the category. Refuses to delete if any non-deleted templates still reference it. */
    void delete(String id);

    /** Count of non-deleted templates pointing at this category — used by delete-guard logic. */
    long countTemplates(String categoryId);
}
