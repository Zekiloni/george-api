package com.zekiloni.george.platform.application.port.out.page;

import com.zekiloni.george.platform.domain.model.page.PageTemplate;
import com.zekiloni.george.platform.domain.model.page.TemplateSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PageTemplateRepositoryPort {
    PageTemplate save(PageTemplate template);
    Page<PageTemplate> findAll(Pageable pageable);
    Optional<PageTemplate> findById(String id);
    Optional<PageTemplate> findByTitleAndSource(String title, TemplateSource source);
    void deleteById(String id);
}
