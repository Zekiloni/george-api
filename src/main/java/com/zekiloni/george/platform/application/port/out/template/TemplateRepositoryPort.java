package com.zekiloni.george.platform.application.port.out.template;

import com.zekiloni.george.platform.domain.model.template.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TemplateRepositoryPort {

    Optional<Template> findById(String id);

    Template save(Template template);

    void delete(String id);

    /**
     * Search across the library:
     * <ul>
     *   <li>Public templates (category_id IS NOT NULL) — visible to everyone</li>
     *   <li>Owned templates (tenant_id = currentTenant) — only to that tenant</li>
     * </ul>
     * Free-text filter matches {@code name ILIKE '%q%'} via the trigram index.
     * Category filter, when set, restricts to public templates inside that
     * category (private templates have no category by design).
     *
     * @param currentTenant   the calling tenant (never null)
     * @param query           free-text search, nullable
     * @param categoryId      filter to public templates in this category, nullable
     * @param ownedOnly       when true, return only the caller's owned templates
     * @param pageable        paging + sort
     */
    Page<Template> search(String currentTenant, String query, String categoryId, boolean ownedOnly, Pageable pageable);

    /** Atomically increments {@code usage_count} by 1. */
    void incrementUsage(String templateId);
}
