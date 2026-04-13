package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.workspace.application.port.in.PageQueryUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageQueryService implements PageQueryUseCase {
    private final PageRepositoryPort repository;

    @Override
    public Optional<Page> handle(String id) {
        return repository.findById(id);
    }

    @Override
    public org.springframework.data.domain.Page<Page> handle(Pageable pageable) {
        return repository.findAll(pageable);
    }
}
