package com.zekiloni.george.platform.infrastructure.in.web;

import com.zekiloni.george.platform.application.port.in.template.CreateCampaignFromTemplateUseCase;
import com.zekiloni.george.platform.application.port.in.template.TemplateMutationUseCase;
import com.zekiloni.george.platform.application.port.in.template.TemplateQueryUseCase;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.CreateCampaignFromTemplateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.TemplateDto;
import com.zekiloni.george.platform.infrastructure.in.web.dto.template.TemplateUpsertDto;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.CampaignDtoMapper;
import com.zekiloni.george.platform.infrastructure.in.web.mapper.TemplateDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("${api.base.path:/api/v1}/templates")
@RequiredArgsConstructor
public class TemplateApiController {

    private final TemplateQueryUseCase queryUseCase;
    private final TemplateMutationUseCase mutationUseCase;
    private final CreateCampaignFromTemplateUseCase createCampaignFromTemplateUseCase;
    private final TemplateDtoMapper mapper;
    private final CampaignDtoMapper campaignMapper;

    /**
     * Library search. Defaults return a single mixed page of public + owned
     * templates; pass {@code owned=true} for the user's private locker only,
     * or {@code categoryId=…} to drill into a public category.
     */
    @GetMapping
    public ResponseEntity<Page<TemplateDto>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String categoryId,
            @RequestParam(defaultValue = "false") boolean owned,
            Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.search(q, categoryId, owned, pageable).map(mapper::toDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TemplateDto> findById(@PathVariable String id) {
        return queryUseCase.findById(id)
                .map(mapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** Creates a private template owned by the calling tenant. */
    @PostMapping
    public ResponseEntity<TemplateDto> createOwned(@Valid @RequestBody TemplateUpsertDto dto) {
        var created = mutationUseCase.createOwned(mapper.toDomain(dto));
        return ResponseEntity.created(URI.create("/api/v1/templates/" + created.getId()))
                .body(mapper.toDto(created));
    }

    /** Admin-only: creates a public catalog template inside a category. */
    @PostMapping("/public")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TemplateDto> createPublic(@Valid @RequestBody TemplateUpsertDto dto) {
        var created = mutationUseCase.createPublic(mapper.toDomain(dto), dto.categoryId());
        return ResponseEntity.created(URI.create("/api/v1/templates/" + created.getId()))
                .body(mapper.toDto(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TemplateDto> update(@PathVariable String id,
                                              @Valid @RequestBody TemplateUpsertDto dto) {
        return ResponseEntity.ok(mapper.toDto(mutationUseCase.update(id, mapper.toDomain(dto))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        mutationUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    /** Admin-only: moves a tenant template into the public catalog. */
    @PostMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TemplateDto> publish(@PathVariable String id,
                                               @RequestParam String categoryId) {
        return ResponseEntity.ok(mapper.toDto(mutationUseCase.publish(id, categoryId)));
    }

    /**
     * Instantiates the template into a fresh tenant-owned Campaign. The body
     * carries the campaign's name + values for any variables the template
     * declares. Returns the new campaign (in SCHEDULED status with no
     * recipients yet — tenant fills those in via the campaign-edit flow).
     */
    @PostMapping("/{id}/use")
    public ResponseEntity<?> createCampaign(@PathVariable String id,
                                            @Valid @RequestBody CreateCampaignFromTemplateDto dto) {
        var campaign = createCampaignFromTemplateUseCase.handle(id, dto.campaignName(), dto.variables());
        return ResponseEntity.ok(campaignMapper.toDto(campaign));
    }
}
