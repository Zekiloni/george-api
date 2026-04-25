package com.zekiloni.george.platform.application.usecase.lead;

import com.zekiloni.george.platform.application.port.in.lead.LeadQueryUseCase;
import com.zekiloni.george.platform.application.port.out.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.model.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LeadQueryService implements LeadQueryUseCase {
    private final LeadRepositoryPort repository;

    @Override
    public Page<Lead> handle(Pageable pageable, LeadSpecification specification) {
        return repository.findAll(pageable, specification);
    }
}
