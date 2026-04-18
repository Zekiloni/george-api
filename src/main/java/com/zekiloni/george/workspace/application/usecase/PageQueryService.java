package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.workspace.application.port.in.PageQueryUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Servis za pronalaženje i čitanje stranica iz baze.
 * Implementira PageQueryUseCase port interfejs.
 */
@Service
@RequiredArgsConstructor
public class PageQueryService implements PageQueryUseCase {

    private final PageRepositoryPort repository;

    @Override
    public Optional<Page> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Page> findBySlug(String slug) {
        return repository.findBySlug(slug);
    }

    @Override
    public org.springframework.data.domain.Page<Page> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
