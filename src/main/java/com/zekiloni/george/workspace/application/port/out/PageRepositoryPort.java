package com.zekiloni.george.workspace.application.port.out;

import com.zekiloni.george.workspace.domain.page.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PageRepositoryPort {
    Page save(Page page);
    Optional<Page> findById(String id);
    org.springframework.data.domain.Page<Page> findAll(Pageable pageable);
    void deleteById(String id);
}
