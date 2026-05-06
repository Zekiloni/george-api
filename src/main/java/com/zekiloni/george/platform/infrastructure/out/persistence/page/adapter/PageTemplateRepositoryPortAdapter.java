package com.zekiloni.george.platform.infrastructure.out.persistence.page.adapter;

import com.zekiloni.george.platform.application.port.out.page.PageTemplateRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.mapper.PageTemplateEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.repository.PageTemplateJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PageTemplateRepositoryPortAdapter implements PageTemplateRepositoryPort {

    private final PageTemplateJpaRepository repository;
    private final PageTemplateEntityMapper mapper;

    @Override
    public PageTemplate save(PageTemplate template) {
        return mapper.toDomain(repository.save(mapper.toEntity(template)));
    }

    @Override
    public Page<PageTemplate> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public Optional<PageTemplate> findById(String id) {
        return parseUuid(id).flatMap(repository::findById).map(mapper::toDomain);
    }

    @Override
    public Optional<PageTemplate> findByTitleAndSource(String title, TemplateSource source) {
        return repository.findByTitleAndSource(title, source).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        parseUuid(id).ifPresent(repository::deleteById);
    }

    private static Optional<UUID> parseUuid(String id) {
        if (id == null || id.isBlank()) return Optional.empty();
        try {
            return Optional.of(UUID.fromString(id));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
