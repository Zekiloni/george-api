package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.workspace.application.port.in.PageCreateUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.PageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageCreateService implements PageCreateUseCase {
    private final PageRepositoryPort repository;

    @Override
    public Page handle(Page page) {
        page.setStatus(PageStatus.PUBLISHED);
        return repository.save(page);
    }
}
