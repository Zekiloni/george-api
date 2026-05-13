package com.zekiloni.george.platform.infrastructure.out.persistence.template.adapter;

import com.zekiloni.george.platform.application.port.out.template.CategoryRepositoryPort;
import com.zekiloni.george.platform.domain.model.template.Category;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.entity.CategoryEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.mapper.CategoryEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.repository.CategoryJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CategoryRepositoryPortAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpaRepository;
    private final CategoryEntityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return mapper.toDomain(jpaRepository.findAllOrdered());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Category> findBySlug(String parentId, String slug) {
        UUID parent = parentId == null ? null : UUID.fromString(parentId);
        return jpaRepository.findByParentIdAndSlug(parent, slug).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Category save(Category category) {
        CategoryEntity entity = mapper.toEntity(category);
        return mapper.toDomain(jpaRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(String id) {
        jpaRepository.deleteById(UUID.fromString(id));
    }

    @Override
    @Transactional(readOnly = true)
    public long countTemplates(String categoryId) {
        return jpaRepository.countTemplates(UUID.fromString(categoryId));
    }
}
