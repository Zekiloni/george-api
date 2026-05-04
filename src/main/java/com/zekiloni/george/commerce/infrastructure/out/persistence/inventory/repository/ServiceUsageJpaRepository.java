package com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.repository;

import com.zekiloni.george.commerce.infrastructure.out.persistence.inventory.entity.ServiceUsageEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ServiceUsageJpaRepository extends JpaRepository<ServiceUsageEntity, UUID> {

    Optional<ServiceUsageEntity> findByServiceAccessId(String serviceAccessId);

    /**
     * Pessimistic-lock the usage row for the duration of the transaction.
     * Used by the quota service to atomically reserve/release a batch.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT u FROM ServiceUsageEntity u WHERE u.serviceAccessId = :serviceAccessId")
    Optional<ServiceUsageEntity> lockByServiceAccessId(@Param("serviceAccessId") String serviceAccessId);
}
