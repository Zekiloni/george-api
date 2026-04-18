package com.zekiloni.george.workspace.infrastructure.input.web.page.dto;

import com.zekiloni.george.workspace.domain.page.PageStatus;
import com.zekiloni.george.workspace.domain.page.definition.PageDefinition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * Web layer DTO za čitanje stranice.
 * Koristi se za serijalizaciju Page entiteta u JSON odgovoru.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageDto {

    /**
     * Jedinstveni identifikator stranice
     */
    private String id;

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
     * Kompletan definiciju stranice
     */
    private PageDefinition definition;

    /**
     * ID korisnika koji je kreirao stranicu
     */
    private String createdBy;

    /**
     * Verzija stranice
     */
    private Integer version;

    /**
     * Timestamp kreiranja
     */
    private OffsetDateTime createdAt;

    /**
     * Timestamp posljednje izmjene
     */
    private OffsetDateTime updatedAt;
}
