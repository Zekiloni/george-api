package com.zekiloni.george.platform.infrastructure.out.persistence.lead.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LeadJpaRepository extends JpaRepository<LeadEntity, String>, JpaSpecificationExecutor<LeadEntity> {
}
