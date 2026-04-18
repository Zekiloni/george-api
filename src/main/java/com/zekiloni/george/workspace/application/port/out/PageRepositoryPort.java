package com.zekiloni.george.workspace.application.port.out;

import com.zekiloni.george.workspace.domain.page.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Port interfejs za upravljanje stranicama u bazi podataka.
 * Implementira se kroz PageRepositoryPortAdapter sa PageJpaRepository.
 */
public interface PageRepositoryPort {

    /**
     * Čuva novu ili ažurira postojeću stranicu.
     */
    Page save(Page page);

    /**
     * Pronalazi stranicu po ID-u.
     */
    Optional<Page> findById(String id);

    /**
     * Pronalazi stranicu po slug-u.
     */
    Optional<Page> findBySlug(String slug);

    /**
     * Pronalazi sve stranice sa paginacijom.
     */
    org.springframework.data.domain.Page<Page> findAll(Pageable pageable);

    /**
     * Briše stranicu po ID-u.
     */
    void deleteById(String id);

    /**
     * Proverava da li stranica postoji po ID-u.
     */
    boolean existsById(String id);

    /**
     * Proverava da li stranica sa datim slug-om postoji.
     */
    boolean existsBySlug(String slug);
}
