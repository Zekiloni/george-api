package com.zekiloni.george.workspace.application.port.in;

import com.zekiloni.george.workspace.domain.page.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PageQueryUseCase {
    Optional<Page> handle(String id);
    org.springframework.data.domain.Page<Page> handle(Pageable pageable);
}
