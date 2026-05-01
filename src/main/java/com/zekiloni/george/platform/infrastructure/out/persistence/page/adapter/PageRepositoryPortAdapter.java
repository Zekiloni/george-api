package com.zekiloni.george.platform.infrastructure.out.persistence.page.adapter;

import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.repository.PageJpaRepository;
import com.zekiloni.george.platform.infrastructure.out.persistence.page.mapper.PageEntityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class PageRepositoryPortAdapter implements PageRepositoryPort {

    private final PageJpaRepository repository;
    private final PageEntityMapper mapper;

    @Override
    public Page save(Page page) {
        return mapper.toDomain(repository.save(mapper.toEntity(page)));
    }

    @Override
    public Optional<Page> findById(String id) {
        return parseUuid(id)
            .flatMap(repository::findById)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<Page> findBySlug(String slug) {
        return repository.findBySlug(slug).map(mapper::toDomain);
    }

    @Override
    public org.springframework.data.domain.Page<Page> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        parseUuid(id).ifPresent(repository::deleteById);
    }

    @Override
    public boolean existsById(String id) {
        return parseUuid(id).map(repository::existsById).orElse(false);
    }

    @Override
    public boolean existsBySlug(String slug) {
        return repository.findBySlug(slug).isPresent();
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
