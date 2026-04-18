package com.zekiloni.george.workspace.application.port.in;

import com.zekiloni.george.workspace.domain.page.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Use case port za pronalaženje i čitanje stranica.
 * Implementira se kroz PageQueryService.
 */
public interface PageQueryUseCase {

    /**
     * Pronalazi stranicu po ID-u.
     *
     * @param id Jedinstveni identifikator stranice
     * @return Optional sa Page-om ako postoji
     */
    Optional<Page> findById(String id);

    /**
     * Pronalazi stranicu po URL-friendly slug-u.
     *
     * @param slug URL identifikator stranice
     * @return Optional sa Page-om ako postoji
     */
    Optional<Page> findBySlug(String slug);

    /**
     * Pronalazi sve stranice sa paginacijom i sortiranjem.
     *
     * @param pageable Paginacijska specifikacija
     * @return Stranica sa listom Page objekata
     */
    org.springframework.data.domain.Page<Page> findAll(Pageable pageable);
}
