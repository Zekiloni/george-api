package com.zekiloni.george.platform.application.port.in.page;

import com.zekiloni.george.platform.domain.model.page.Page;
import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PageTemplateUseCase {

    PageTemplate upsertBuiltin(String title, PageTemplate template);

    org.springframework.data.domain.Page<PageTemplate> findAll(Pageable pageable);

    Optional<PageTemplate> findById(String id);

    Optional<PageTemplate> findByTitleAndSource(String title, TemplateSource source);

    /**
     * Materialize a template into a live {@link Page}. Generates a fresh slug,
     * sets {@code status=DRAFT} and {@code createdBy} from the caller. Does not
     * touch the source template.
     */
    Page cloneToPage(String templateId, String slugOverride, String createdBy);

    void deleteById(String id);
}
