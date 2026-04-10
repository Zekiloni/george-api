package com.zekiloni.george.platform.application.port.in;


import com.zekiloni.george.platform.domain.model.Lead;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface LeadQueryUseCase {
    Page<Lead> handle(Pageable pageable, LeadSpecification specification);
    List<Lead> handle(int limit, LeadSpecification specification);
}
