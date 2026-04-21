package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.repository;

import com.zekiloni.george.commerce.domain.catalog.model.ServiceSpecification;
import com.zekiloni.george.commerce.domain.inventory.model.ServiceStatus;
import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

@Repository
public interface ServiceAccessJpaRepository
    extends JpaRepository<ServiceAccessEntity, UUID> {

    @Modifying
    @Query("""
        UPDATE ServiceAccessEntity s
        SET s.status = :newStatus,
            s.updatedAt = :now
        WHERE s.status = :oldStatus
          AND s.validTo IS NOT NULL
          AND s.validTo BETWEEN :gracePeriodEnd AND :now
        """)
    int updateToSuspended(
            @Param("oldStatus") ServiceStatus oldStatus,
            @Param("newStatus") ServiceStatus newStatus,
            @Param("now") OffsetDateTime now,
            @Param("gracePeriodEnd") OffsetDateTime gracePeriodEnd
    );

    @Query("""
        SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END
        FROM ServiceAccessEntity s
        WHERE s.serviceSpecification = :serviceSpecification
          AND s.status = 'ACTIVE'
""")
    boolean hasActiveAccess(@Param("serviceSpecification") ServiceSpecification serviceSpecification);

    @Modifying
    @Query("""
        UPDATE ServiceAccessEntity s
        SET s.status = :newStatus,
            s.updatedAt = :now
        WHERE s.status = :oldStatus
          AND s.validTo IS NOT NULL
          AND s.validTo < :gracePeriodEnd
        """)
    int updateToTerminated(
            @Param("oldStatus") ServiceStatus oldStatus,
            @Param("newStatus") ServiceStatus newStatus,
            @Param("now") OffsetDateTime now,
            @Param("gracePeriodEnd") OffsetDateTime gracePeriodEnd
    );
}