package com.zekiloni.george.platform.application.usecase.template;

import com.zekiloni.george.platform.application.port.in.template.CategoryMutationUseCase;
import com.zekiloni.george.platform.application.port.out.template.CategoryRepositoryPort;
import com.zekiloni.george.platform.domain.model.template.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class CategoryMutationService implements CategoryMutationUseCase {

    private final CategoryRepositoryPort repository;

    @Override
    public Category create(Category category) {
        // null id forces the generator to assign a fresh UUID
        category.setId(null);
        return repository.save(category);
    }

    @Override
    public Category update(String id, Category patch) {
        Category existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category not found"));
        if (patch.getName() != null)        existing.setName(patch.getName());
        if (patch.getSlug() != null)        existing.setSlug(patch.getSlug());
        if (patch.getParentId() != null)    existing.setParentId(patch.getParentId());
        existing.setSortOrder(patch.getSortOrder());
        return repository.save(existing);
    }

    @Override
    public void delete(String id) {
        long templateCount = repository.countTemplates(id);
        if (templateCount > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Cannot delete category: " + templateCount + " template(s) still reference it");
        }
        repository.delete(id);
    }
}
