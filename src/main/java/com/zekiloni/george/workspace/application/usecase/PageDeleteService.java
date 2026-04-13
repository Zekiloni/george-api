package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.workspace.application.port.in.PageDeleteUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PageDeleteService implements PageDeleteUseCase {
    private final PageRepositoryPort repository;

    @Override
    public void handle(String id) {
        repository.deleteById(id);
    }
}
