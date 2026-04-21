package com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.repository;

import com.zekiloni.george.commerce.domain.catalog.model.OfferingStatus;
import com.zekiloni.george.commerce.infrastructure.out.persistence.catalog.entity.OfferingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OfferingJpaRepository extends JpaRepository<OfferingEntity, UUID> {
    @Query("SELECT o FROM OfferingEntity o WHERE o.status = :status")
    List<OfferingEntity> findAllByStatus(@Param("status") OfferingStatus status);
}

