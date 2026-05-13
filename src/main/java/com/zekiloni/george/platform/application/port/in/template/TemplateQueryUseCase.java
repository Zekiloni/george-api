package com.zekiloni.george.platform.application.port.in.template;

import com.zekiloni.george.platform.domain.model.template.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface TemplateQueryUseCase {

    Optional<Template> findById(String id);

    /**
     * Library search. Resolves the calling tenant from {@code TenantContext}
     * so callers don't need to thread it through.
     *
     * @param query        free-text name match (nullable)
     * @param categoryId   public-template filter (nullable)
     * @param ownedOnly    when true, returns only the caller's own templates
     * @param pageable     paging/sort
     */
    Page<Template> search(String query, String categoryId, boolean ownedOnly, Pageable pageable);
}
