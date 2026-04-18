package com.zekiloni.george.workspace.application.port.in;

import com.zekiloni.george.workspace.domain.page.Page;
import com.zekiloni.george.workspace.domain.page.dto.PageUpdateDto;

/**
 * Use case port za ažuriranje postojeće stranice.
 * Implementira se kroz PageUpdateService.
 */
public interface PageUpdateUseCase {

    /**
     * Ažurira postojeću stranicu sa novim podacima.
     * Samo polja koja nisu null će biti ažurirana.
     *
     * @param id ID stranice koja se ažurira
     * @param command DTO sa podacima za ažuriranje
     * @return Ažurirana Page entitet
     */
    Page handle(String id, PageUpdateDto command);
}

