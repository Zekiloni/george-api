package com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.repository;

import com.zekiloni.george.provisioning.infrastructure.output.persistence.inventory.entity.ServiceAccessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ServiceAccessJpaRepository
    extends JpaRepository<ServiceAccessEntity, UUID> {
}