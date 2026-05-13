package com.zekiloni.george.platform.application.port.in.template;

import com.zekiloni.george.platform.domain.model.template.Category;

import java.util.List;
import java.util.Optional;

public interface CategoryQueryUseCase {

    /** Flat list ordered by (parentId, sortOrder, name). */
    List<Category> findAll();

    /**
     * Returns the category tree rooted at top-level categories (parent IS NULL).
     * Each Category's {@code children} list is populated recursively.
     */
    List<Category> findTree();

    Optional<Category> findById(String id);
}
