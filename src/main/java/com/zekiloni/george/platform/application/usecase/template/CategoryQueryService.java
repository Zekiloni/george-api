package com.zekiloni.george.platform.application.usecase.template;

import com.zekiloni.george.platform.application.port.in.template.CategoryQueryUseCase;
import com.zekiloni.george.platform.application.port.out.template.CategoryRepositoryPort;
import com.zekiloni.george.platform.domain.model.template.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryQueryService implements CategoryQueryUseCase {

    private final CategoryRepositoryPort repository;

    @Override
    public List<Category> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Category> findTree() {
        List<Category> flat = repository.findAll();
        // Index by id for O(1) parent lookup; flat list comes pre-ordered so
        // a single pass produces a stable, sorted tree.
        Map<String, Category> byId = new HashMap<>();
        for (Category c : flat) {
            c.setChildren(new ArrayList<>());
            byId.put(c.getId(), c);
        }
        List<Category> roots = new ArrayList<>();
        for (Category c : flat) {
            if (c.getParentId() == null) {
                roots.add(c);
            } else {
                Category parent = byId.get(c.getParentId());
                if (parent != null) parent.getChildren().add(c);
            }
        }
        return roots;
    }

    @Override
    public Optional<Category> findById(String id) {
        return repository.findById(id);
    }
}
