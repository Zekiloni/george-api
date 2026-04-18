package com.zekiloni.george.workspace.application.port.in;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.dto.PageCreateDto;

/**
 * Use case port za kreiranje nove stranice.
 * Implementira se kroz PageCreateService.
 */
public interface PageCreateUseCase {

    /**
     * Kreira novu stranicu sa prosleđenim definicijama.
     *
     * @param command DTO sa podacima za novu stranicu
     * @return Kreirani Page entitet sa ID-om i metadata-om
     */
    Page handle(PageCreateDto command);
}
