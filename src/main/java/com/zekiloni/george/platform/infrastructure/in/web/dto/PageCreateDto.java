package com.zekiloni.george.platform.infrastructure.in.web.dto;

import com.zekiloni.george.platform.domain.page.definition.PageDefinition;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Web layer DTO za kreiranje nove stranice.
 * Validira ulazne podatke pre nego što se prosleđuju na aplikacijski sloj.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PageCreateDto {

    /**
     * Naslov stranice
     */
    @NotBlank(message = "Title is required")
    private String title;

    /**
     * URL-friendly slug
     */
    @NotBlank(message = "Slug is required")
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
     * Inicijalna definicija stranice
     */
    @NotNull(message = "Page definition is required")
    @Valid
    private PageDefinition definition;
}
