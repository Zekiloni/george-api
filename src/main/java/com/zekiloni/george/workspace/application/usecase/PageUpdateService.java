package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.workspace.application.port.in.PageUpdateUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PageUpdateService implements PageUpdateUseCase {
    private final PageRepositoryPort repository;

    @Override
    public Page handle(String id, Page command) {

        Page existingPage = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Page with id '" + id + "' not found"));

        return repository.save(existingPage);
    }
}

