package com.zekiloni.george.workspace.infrastructure.input.web.page;

import com.zekiloni.george.workspace.application.port.in.PageCreateUseCase;
import com.zekiloni.george.workspace.application.port.in.PageDeleteUseCase;
import com.zekiloni.george.workspace.application.port.in.PageQueryUseCase;
import com.zekiloni.george.workspace.application.port.in.PageUpdateUseCase;
import com.zekiloni.george.workspace.domain.page.dto.PageCreateDto;
import com.zekiloni.george.workspace.domain.page.dto.PageUpdateDto;
import com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageDto;

import com.zekiloni.george.workspace.infrastructure.input.web.page.mapper.PageDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API kontroler za upravljanje stranicama.
 * Pruža CRUD operacije za Page entitete sa novom PageDefinition strukturom.
 */
@RestController
@RequestMapping("${api.base.path:/api/v1}/page")
@RequiredArgsConstructor
public class PageApiController {

    private final PageDtoMapper webDtoMapper;
    private final PageCreateUseCase createUseCase;
    private final PageQueryUseCase queryUseCase;
    private final PageUpdateUseCase updateUseCase;
    private final PageDeleteUseCase deleteUseCase;

    /**
     * Kreira novu stranicu.
     * POST /api/v1/pages
     */
    @PostMapping
    public ResponseEntity<PageDto> create(
        @RequestBody @Valid com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageCreateDto webDto
    ) {
        // Mapiranje web DTO na domain DTO
        var domainDto = PageCreateDto.builder()
            .title(webDto.getTitle())
            .slug(webDto.getSlug())
            .description(webDto.getDescription())
            .keywords(webDto.getKeywords())
            .faviconUrl(webDto.getFaviconUrl())
            .definition(webDto.getDefinition())
            .build();

        var page = createUseCase.handle(domainDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(webDtoMapper.toDto(page));
    }

    /**
     * Pronalazi sve stranice sa paginacijom.
     * GET /api/v1/pages?page=0&size=20&sort=createdAt,desc
     */
    @GetMapping
    public ResponseEntity<Page<PageDto>> findAll(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.findAll(pageable).map(webDtoMapper::toDto));
    }

    /**
     * Pronalazi stranicu po ID-u.
     * GET /api/v1/pages/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PageDto> findById(@PathVariable String id) {
        return queryUseCase.findById(id)
            .map(webDtoMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Pronalazi stranicu po slug-u.
     * GET /api/v1/pages/slug/{slug}
     */
    @GetMapping("/slug/{slug}")
    public ResponseEntity<PageDto> findBySlug(@PathVariable String slug) {
        return queryUseCase.findBySlug(slug)
            .map(webDtoMapper::toDto)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Ažurira stranicu.
     * PATCH /api/v1/pages/{id}
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PageDto> update(
        @PathVariable String id,
        @RequestBody @Valid com.zekiloni.george.workspace.infrastructure.input.web.page.dto.PageUpdateDto webDto
    ) {
        // Mapiranje web DTO na domain DTO
        var domainDto = PageUpdateDto.builder()
            .title(webDto.getTitle())
            .slug(webDto.getSlug())
            .description(webDto.getDescription())
            .keywords(webDto.getKeywords())
            .faviconUrl(webDto.getFaviconUrl())
            .status(webDto.getStatus())
            .definition(webDto.getDefinition())
            .build();

        var page = updateUseCase.handle(id, domainDto);
        return ResponseEntity.ok(webDtoMapper.toDto(page));
    }

    /**
     * Briše stranicu.
     * DELETE /api/v1/pages/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteUseCase.handle(id);
        return ResponseEntity.noContent().build();
    }
}
