package com.zekiloni.george.platform.application.port.out;

import com.zekiloni.george.platform.domain.model.page.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;


public interface PageRepositoryPort {
    Page save(Page page);
    Optional<Page> findById(String id);
    Optional<Page> findBySlug(String slug);

    org.springframework.data.domain.Page<Page> findAll(Pageable pageable);

    void deleteById(String id);

    boolean existsById(String id);

    boolean existsBySlug(String slug);
}
