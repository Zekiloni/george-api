package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.provisioning.application.usecase.ServiceAccessQueryService;
import com.zekiloni.george.provisioning.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.workspace.application.port.in.PageUpdateUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.dto.PageUpdateDto;
import com.zekiloni.george.workspace.domain.page.mapper.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

/**
 * Servis za ažuriranje postojećih stranica.
 * Validira pristup, sprečava duplicate slug-ove, inkrementira verziju.
 */
@Service
@RequiredArgsConstructor
public class PageUpdateService implements PageUpdateUseCase {

    private final PageRepositoryPort repository;
    private final PageMapper pageMapper;
    private final ServiceAccessQueryService serviceAccessQueryService;

    @Override
    public Page handle(String id, PageUpdateDto command) {
        // Validacija pristupa
        if (!serviceAccessQueryService.hasActiveAccess(ServiceSpecification.PAGE)) {
            throw new IllegalStateException("No active service access for the given specification");
        }

        // Pronalaženje postojeće stranice
        Page existingPage = repository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Page with id '" + id + "' not found"));

        // Ako se menja slug, validacija da novi slug nije vec u upotrebi
        if (command.getSlug() != null && !command.getSlug().equals(existingPage.getSlug())) {
            if (repository.existsBySlug(command.getSlug())) {
                throw new IllegalArgumentException("A page with slug '" + command.getSlug() + "' already exists");
            }
        }

        // Mapiranje DTO vrednosti na postojeći Page objekat
        pageMapper.updateEntityFromDto(command, existingPage);

        // Ako se promenila definition, inkrementiramo verziju
        if (command.getDefinition() != null) {
            existingPage.setVersion(existingPage.getVersion() + 1);
        }

        // Ažuriranje timestamp-a
        existingPage.setUpdatedAt(OffsetDateTime.now());

        // Čuvanje u bazi
        return repository.save(existingPage);
    }
}

