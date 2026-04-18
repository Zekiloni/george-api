package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.provisioning.application.usecase.ServiceAccessQueryService;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.workspace.application.port.in.PageCreateUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.PageStatus;
import com.zekiloni.george.workspace.domain.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.domain.page.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Servis za kreiranje novih stranica.
 * Validira pristup, sprečava duplicate slug-ove, i postavlja inicijalne vrednosti.
 */
@Service
@RequiredArgsConstructor
public class PageCreateService implements PageCreateUseCase {

    private final PageRepositoryPort repository;
    private final PageMapper pageMapper;
    private final ServiceAccessQueryService serviceAccessQueryService;

    @Override
    public Page handle(PageCreateDto command) {
        // Validacija pristupa
        if (!serviceAccessQueryService.hasActiveAccess(ServiceSpecification.PAGE)) {
            throw new IllegalStateException("No active service access for the given specification");
        }

        // Validacija da slug nije vec u upotrebi
        if (repository.existsBySlug(command.getSlug())) {
            throw new IllegalArgumentException("A page with slug '" + command.getSlug() + "' already exists");
        }

        // Mapiranje DTO na domenski model
        Page page = pageMapper.toEntity(command);

        // Postavljanje inicijanialnih vrednosti
        page.setId(UUID.randomUUID().toString());
        page.setStatus(PageStatus.DRAFT);
        page.setVersion(1);
        page.setCreatedAt(OffsetDateTime.now());
        page.setUpdatedAt(OffsetDateTime.now());

        // Čuvanje u bazi
        return repository.save(page);
    }
}
