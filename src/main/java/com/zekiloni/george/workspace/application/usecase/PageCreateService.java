package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.provisioning.application.usecase.ServiceAccessQueryService;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.workspace.application.port.in.PageCreateUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
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
