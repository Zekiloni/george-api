package com.zekiloni.george.platform.application.usecase.page;

import com.zekiloni.george.platform.application.port.in.page.PageUpdateUseCase;
import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PageUpdateService implements PageUpdateUseCase {
    private final PageRepositoryPort repository;

    @Override
    public Page handle(String id, Page command) {
        if (repository.findById(id).isEmpty()) {
            throw new IllegalArgumentException("Page with id '" + id + "' not found");
        }
        command.setId(id);
        return repository.save(command);
    }
}

