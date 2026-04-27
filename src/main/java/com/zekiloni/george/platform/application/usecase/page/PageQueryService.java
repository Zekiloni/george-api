package com.zekiloni.george.platform.application.usecase.page;

import com.zekiloni.george.platform.application.port.in.page.PageQueryUseCase;
import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.Page;
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
