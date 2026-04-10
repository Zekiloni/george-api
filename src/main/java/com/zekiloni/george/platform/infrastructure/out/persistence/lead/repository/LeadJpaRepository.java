package com.zekiloni.george.platform.infrastructure.out.persistence.lead.repository;

import com.zekiloni.george.platform.infrastructure.out.persistence.lead.entity.LeadEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeadJpaRepository extends JpaRepository<LeadEntity, UUID>, JpaSpecificationExecutor<LeadEntity> {
}
