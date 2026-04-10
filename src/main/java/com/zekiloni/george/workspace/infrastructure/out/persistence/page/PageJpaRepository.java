package com.zekiloni.george.workspace.infrastructure.out.persistence.page;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PageJpaRepository extends JpaRepository<PageEntity, UUID> {
}
