package com.zekiloni.george.platform.application.port.in.lead;


import com.zekiloni.george.platform.domain.model.lead.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface LeadQueryUseCase {
    Page<Lead> handle(Pageable pageable, LeadSpecification specification);
}
