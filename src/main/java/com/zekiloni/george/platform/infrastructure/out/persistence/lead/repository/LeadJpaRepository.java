package com.zekiloni.george.platform.infrastructure.out.persistence.lead.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadSpecification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LeadJpaRepository extends JpaRepository<LeadEntity, String>, JpaSpecificationExecutor<LeadEntity> {
}
