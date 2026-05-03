package com.zekiloni.george.platform.infrastructure.in.web.gateway;

import com.zekiloni.george.platform.application.port.in.gateway.GatewayCreateUseCase;
import com.zekiloni.george.platform.application.port.in.gateway.GatewayQueryUseCase;
import com.zekiloni.george.platform.domain.model.gateway.Gateway;
import com.zekiloni.george.platform.domain.model.gateway.GatewayConfigKeys;
import com.zekiloni.george.platform.domain.model.gateway.GatewayType;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmGateway;
import com.zekiloni.george.platform.domain.model.gateway.gsm.GsmProvider;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGateway;
import com.zekiloni.george.platform.domain.model.gateway.smtp.SmtpGatewayProvider;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayCreateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.dto.GatewayUpdateDto;
import com.zekiloni.george.platform.infrastructure.in.web.gateway.mapper.GatewayDtoMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("${api.base.path:/api/v1}/gateway")
@RequiredArgsConstructor
public class GatewayApiController {

    private final GatewayCreateUseCase createUseCase;
    private final GatewayQueryUseCase queryUseCase;
    private final GatewayDtoMapper mapper;

    @PostMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GatewayDto> createGateway(@RequestBody @Valid GatewayCreateDto dto) {
        Gateway gateway = createUseCase.create(mapper.toDomain(dto));
        return ResponseEntity.ok(mapper.toDto(gateway));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<GatewayDto> updateGateway(@PathVariable String id,
                                                    @RequestBody @Valid GatewayUpdateDto dto) {
        Gateway gateway = queryUseCase.findById(id);
        if (dto.name() != null) gateway.setName(dto.name());
        if (dto.description() != null) gateway.setDescription(dto.description());
        if (dto.priority() != null) gateway.setPriority(dto.priority());
        if (dto.config() != null) gateway.setConfig(mergeConfig(gateway.getConfig(), dto.config()));

        switch (dto) {
            case GatewayUpdateDto.SmtpUpdate s when gateway instanceof SmtpGateway smtp -> {
                if (s.provider() != null) smtp.setProvider(s.provider());
            }
            case GatewayUpdateDto.GsmUpdate g when gateway instanceof GsmGateway gsm -> {
                if (g.provider() != null) gsm.setProvider(g.provider());
            }
            default -> throw new IllegalArgumentException(
                    "Update payload type does not match gateway " + id + " (" + gateway.getType() + ")");
        }
        return ResponseEntity.ok(mapper.toDto(createUseCase.update(gateway)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> deleteGateway(@PathVariable String id) {
        createUseCase.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> enableGateway(@PathVariable String id) {
        createUseCase.enable(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Void> disableGateway(@PathVariable String id) {
        createUseCase.disable(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<GatewayDto> getGateway(@PathVariable String id) {
        return ResponseEntity.ok(mapper.toDto(queryUseCase.findById(id)));
    }

    @GetMapping
    public ResponseEntity<Page<GatewayDto>> getAllGateways(Pageable pageable) {
        return ResponseEntity.ok(queryUseCase.findAll(pageable).map(mapper::toDto));
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<GatewayDto>> getGatewaysByType(@PathVariable GatewayType type) {
        return ResponseEntity.ok(queryUseCase.findByType(type).stream().map(mapper::toDto).toList());
    }

    @GetMapping("/active")
    public ResponseEntity<List<GatewayDto>> getActiveGateways() {
        return ResponseEntity.ok(queryUseCase.findActive().stream().map(mapper::toDto).toList());
    }

    @GetMapping("/least-loaded/{type}")
    public ResponseEntity<GatewayDto> getLeastLoadedGateway(@PathVariable GatewayType type) {
        return ResponseEntity.ok(mapper.toDto(queryUseCase.findLeastLoaded(type)));
    }

    public record ProviderSchema(Set<String> publicKeys, Set<String> secretKeys) {}

    @GetMapping("/providers")
    public ResponseEntity<Map<GatewayType, Map<String, ProviderSchema>>> listProviders() {
        Map<String, ProviderSchema> smtp = new LinkedHashMap<>();
        for (SmtpGatewayProvider p : SmtpGatewayProvider.values()) {
            smtp.put(p.name(), new ProviderSchema(p.publicKeys(), p.secretKeys()));
        }
        Map<String, ProviderSchema> gsm = new LinkedHashMap<>();
        for (GsmProvider p : GsmProvider.values()) {
            gsm.put(p.name(), new ProviderSchema(p.publicKeys(), p.secretKeys()));
        }
        return ResponseEntity.ok(Map.of(
                GatewayType.SMTP, smtp,
                GatewayType.GSM,  gsm
        ));
    }

    // Merge update map into current, but never overwrite a real secret with the redacted placeholder.
    private static Map<String, String> mergeConfig(Map<String, String> current, Map<String, String> incoming) {
        Map<String, String> merged = current == null ? new HashMap<>() : new HashMap<>(current);
        for (Map.Entry<String, String> e : incoming.entrySet()) {
            if (GatewayConfigKeys.REDACTED.equals(e.getValue())) continue;
            merged.put(e.getKey(), e.getValue());
        }
        return merged;
    }
}
