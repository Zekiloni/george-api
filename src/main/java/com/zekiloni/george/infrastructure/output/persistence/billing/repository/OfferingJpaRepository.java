package com.zekiloni.george.infrastructure.output.persistence.billing.repository;

import com.zekiloni.george.infrastructure.output.persistence.billing.entity.OfferingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OfferingJpaRepository extends JpaRepository<OfferingEntity, String> {
}

