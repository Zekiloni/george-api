package com.zekiloni.george.workspace.application.usecase;

import com.zekiloni.george.workspace.application.port.in.PageDeleteUseCase;
import com.zekiloni.george.workspace.application.port.out.PageRepositoryPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Servis za brisanje stranica.
 * Validira da stranica postoji pre brisanja.
 */
@Service
@RequiredArgsConstructor
public class PageDeleteService implements PageDeleteUseCase {

    private final PageRepositoryPort repository;

    @Override
    public void handle(String id) {
        // Validacija da stranica postoji
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Page with id '" + id + "' not found");
        }

        // Brisanje iz baze
        repository.deleteById(id);
    }
}
