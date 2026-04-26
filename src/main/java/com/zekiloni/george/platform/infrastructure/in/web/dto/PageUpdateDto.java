package com.zekiloni.george.platform.infrastructure.in.web.dto;

import com.zekiloni.george.platform.domain.model.page.PageStatus;
import com.zekiloni.george.platform.domain.model.page.definition.PageDefinition;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web layer DTO za ažuriranje stranice.
 * Sva polja su opciona - samo prosleđena polja će biti ažurirana.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageUpdateDto {

    /**
     * Naslov stranice
     */
    private String title;

    /**
     * URL-friendly slug
     */
    private String slug;

    /**
     * Opis stranice
     */
    private String description;

    /**
     * Meta keywords
     */
    private String keywords;

    /**
     * URL do favicon-a
     */
    private String faviconUrl;

    /**
     * Status stranice
     */
    private PageStatus status;

    /**
     * Ažurirana definicija stranice
     */
    @Valid
    private PageDefinition definition;
}

