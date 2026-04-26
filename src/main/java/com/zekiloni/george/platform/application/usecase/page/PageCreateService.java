package com.zekiloni.george.platform.application.usecase.page;

import com.zekiloni.george.commerce.application.usecase.ServiceAccessQueryService;
import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.platform.application.port.in.page.PageCreateUseCase;
import com.zekiloni.george.platform.application.port.out.PageRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class PageCreateService implements PageCreateUseCase {
    private final PageRepositoryPort repository;
    private final ServiceAccessQueryService serviceAccessQueryService;

    @Override
    public Page handle(Page page) {
        if (!serviceAccessQueryService.hasActiveAccess(ServiceSpecification.PAGE)) {
            throw new IllegalStateException("No active service access for the given specification");
        }

        return repository.save(page);
    }
}
