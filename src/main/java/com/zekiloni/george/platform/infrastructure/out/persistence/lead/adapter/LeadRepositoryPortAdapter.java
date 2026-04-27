package com.zekiloni.george.platform.infrastructure.out.persistence.lead.adapter;

import com.zekiloni.george.common.infrastructure.config.tenant.TenantContext;
import com.zekiloni.george.platform.application.port.out.lead.LeadRepositoryPort;
import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.mapper.LeadEntityMapper;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.repository.LeadJpaRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LeadRepositoryPortAdapter implements LeadRepositoryPort {
    private final LeadJpaRepository repository;
    private final LeadEntityMapper mapper;
    private final TenantContext tenantContext;
    private final EntityManager entityManager;

    @Override
    public Lead save(Lead lead) {
        return mapper.toDomain(repository.save(mapper.toEntity(lead)));
    }

    @Override
    public List<Lead> saveAll(List<Lead> leads) {
        return mapper.toDomain(repository.saveAll(mapper.toEntity(leads)));
    }

    @Override
    public Page<Lead> findAll(Pageable pageable, LeadSpecification specification) {
        applyAccessFilter();
        return repository.findAll(specification, pageable).map(mapper::toDomain);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(UUID.fromString(id));
    }

    private void applyAccessFilter() {
        if (!tenantContext.isSystem()) {
            entityManager.unwrap(Session.class)
                    .enableFilter("tenantAccessFilter")
                    .setParameter("tenantId", tenantContext.getTenantId());
        }
    }
}
