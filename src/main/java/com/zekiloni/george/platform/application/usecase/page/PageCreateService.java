package com.zekiloni.george.platform.application.usecase.page;

import com.zekiloni.george.platform.application.port.in.page.PageCreateUseCase;
import com.zekiloni.george.platform.application.port.out.page.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.domain.model.page.PageStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PageCreateService implements PageCreateUseCase {
    private final PageRepositoryPort repository;

    @Override
    public Page handle(Page page) {
        if (page.getStatus() == null) {
            page.setStatus(PageStatus.DRAFT);
        }
        return repository.save(page);
    }
}
