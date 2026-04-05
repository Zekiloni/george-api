package com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.repository;

import com.zekiloni.george.provisioning.infrastructure.output.persistence.catalog.entity.OfferingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OfferingJpaRepository extends JpaRepository<OfferingEntity, UUID> {
    Optional<OfferingEntity> findByIdentifier(String identifier);
    List<OfferingEntity> findByStatus(String status);
}

