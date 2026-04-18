package com.zekiloni.george.workspace.application.port.in;

/**
 * Use case port za brisanje stranica.
 * Implementira se kroz PageDeleteService.
 */
public interface PageDeleteUseCase {

    /**
     * Briše stranicu po ID-u.
     *
     * @param id Jedinstveni identifikator stranice
     */
    void handle(String id);
}
