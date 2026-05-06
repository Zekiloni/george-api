package com.zekiloni.george.platform.application.usecase.page;

import com.zekiloni.george.platform.application.port.in.page.PageCreateUseCase;
import com.zekiloni.george.platform.application.port.in.page.PageTemplateUseCase;
import com.zekiloni.george.platform.application.port.out.page.PageTemplateRepositoryPort;
import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.domain.model.page.PageStatus;
import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PageTemplateService implements PageTemplateUseCase {

    private final PageTemplateRepositoryPort repository;
    private final PageCreateUseCase pageCreateUseCase;

    @Override
    @Transactional
    public PageTemplate upsertBuiltin(String title, PageTemplate template) {
        template.setSource(TemplateSource.BUILTIN);
        template.setTitle(title);
        return repository.findByTitleAndSource(title, TemplateSource.BUILTIN)
                .map(existing -> {
                    template.setId(existing.getId());
                    template.setCreatedAt(existing.getCreatedAt());
                    return repository.save(template);
                })
                .orElseGet(() -> repository.save(template));
    }

    @Override
    public org.springframework.data.domain.Page<PageTemplate> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Override
    public Optional<PageTemplate> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<PageTemplate> findByTitleAndSource(String title, TemplateSource source) {
        return repository.findByTitleAndSource(title, source);
    }

    @Override
    @Transactional
    public Page cloneToPage(String templateId, String slugOverride, String createdBy) {
        PageTemplate template = repository.findById(templateId)
                .orElseThrow(() -> new NoSuchElementException("PageTemplate not found: " + templateId));

        Page page = Page.builder()
                .title(template.getTitle())
                .slug(slugOverride != null && !slugOverride.isBlank() ? slugOverride : generateSlug(template.getTitle()))
                .description(template.getDescription())
                .keywords(template.getKeywords())
                .faviconUrl(template.getFaviconUrl())
                .definition(template.getDefinition())
                .status(PageStatus.DRAFT)
                .createdBy(createdBy)
                .build();

        return pageCreateUseCase.handle(page);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    private static String generateSlug(String title) {
        if (title == null) return "page-" + System.currentTimeMillis();
        String base = title.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-|-$)", "");
        return base.isEmpty() ? "page-" + System.currentTimeMillis()
                : base + "-" + Long.toString(System.currentTimeMillis() % 10000);
    }
}
