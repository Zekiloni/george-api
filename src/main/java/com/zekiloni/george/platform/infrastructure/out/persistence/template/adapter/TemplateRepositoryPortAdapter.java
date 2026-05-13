package com.zekiloni.george.platform.infrastructure.out.persistence.template.adapter;

import com.zekiloni.george.platform.application.port.out.template.TemplateRepositoryPort;
import com.zekiloni.george.platform.domain.model.template.Template;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.mapper.TemplateEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.template.repository.TemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TemplateRepositoryPortAdapter implements TemplateRepositoryPort {

    private final TemplateJpaRepository jpaRepository;
    private final TemplateEntityMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public Optional<Template> findById(String id) {
        return jpaRepository.findById(UUID.fromString(id)).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public Template save(Template template) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(template)));
    }

    @Override
    @Transactional
    public void delete(String id) {
        jpaRepository.deleteById(UUID.fromString(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Template> search(String currentTenant, String query, String categoryId, boolean ownedOnly, Pageable pageable) {
        UUID category = categoryId == null ? null : UUID.fromString(categoryId);
        String normalized = (query == null || query.isBlank()) ? null : query.trim();
        return jpaRepository.search(currentTenant, normalized, category, ownedOnly, pageable)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional
    public void incrementUsage(String templateId) {
        jpaRepository.incrementUsage(UUID.fromString(templateId));
    }
}
